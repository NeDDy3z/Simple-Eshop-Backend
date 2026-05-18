package cz.cvut.fel.nss.order.facade;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import cz.cvut.fel.nss.order.dto.CheckoutRequest;
import cz.cvut.fel.nss.order.dto.ProductDto;
import cz.cvut.fel.nss.order.exception.EmptyBasketException;
import cz.cvut.fel.nss.order.exception.InsufficientStockException;
import cz.cvut.fel.nss.order.exception.ProductNotFoundException;
import cz.cvut.fel.nss.order.exception.ValidationException;
import cz.cvut.fel.nss.order.model.Order;
import cz.cvut.fel.nss.order.model.OrderItem;
import cz.cvut.fel.nss.order.model.OrderStatus;
import cz.cvut.fel.nss.order.model.User;
import cz.cvut.fel.nss.order.service.OrderService;
import cz.cvut.fel.nss.order.strategy.DiscountStrategy;
import cz.cvut.fel.nss.order.strategy.FixedAmountDiscount;
import cz.cvut.fel.nss.order.strategy.PercentageDiscount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutFacade {

    private final OrderService orderService;
    private final HazelcastInstance hazelcastInstance;

    @Transactional
    public Order processCheckout(User user, CheckoutRequest request) {
        IMap<String, List<CheckoutRequest.CartItem>> baskets = hazelcastInstance.getMap("baskets");
        IMap<Long, ProductDto> products = hazelcastInstance.getMap("products");
        
        List<CheckoutRequest.CartItem> basketItems = baskets.get(user.getUsername());

        if (basketItems == null || basketItems.isEmpty()) {
            throw new EmptyBasketException("Cannot checkout an empty basket");
        }

        // Build Order using the authenticated user
        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.NEW)
                .orderDate(LocalDateTime.now())
                .build();

        // Process items
        for (CheckoutRequest.CartItem cartItem : basketItems) {
            if (cartItem.getQuantity() <= 0) {
                throw new ValidationException("Invalid quantity for product ID: " + cartItem.getProductId());
            }

            // Fetch product from Hazelcast cache
            ProductDto product = products.get(cartItem.getProductId());
            if (product == null) {
                throw new ProductNotFoundException("Product not found in local cache: " + cartItem.getProductId());
            }

            // Optional: check local stock for better UX (real deduction is async via Kafka)
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName() + " (Available: " + product.getStockQuantity() + ", Requested: " + cartItem.getQuantity() + ")");
            }

            // Deduct stock locally in cache for immediate consistency
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            products.put(cartItem.getProductId(), product);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(product.getId())
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(product.getPrice())
                    .build();
            
            order.addOrderItem(orderItem);
        }

        order.calculateTotal();

        // Apply Discount
        if (request != null) {
            DiscountStrategy strategy = getDiscountStrategy(request.getDiscountType(), request.getDiscountValue());
            if (strategy != null) {
                BigDecimal finalAmount = strategy.calculateDiscount(order.getTotalAmount());
                order.setTotalAmount(finalAmount);
            }
        }

        // Create Order
        orderService.createOrder(order);
        
        // Clear basket
        baskets.remove(user.getUsername());
        
        return order;
    }

    private DiscountStrategy getDiscountStrategy(String type, BigDecimal value) {
        if (type == null || value == null) return null;
        return switch (type.toUpperCase()) {
            case "PERCENTAGE" -> new PercentageDiscount(value.doubleValue());
            case "FIXED" -> new FixedAmountDiscount(value);
            default -> null;
        };
    }
}
