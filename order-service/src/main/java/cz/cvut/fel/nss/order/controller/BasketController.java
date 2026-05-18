package cz.cvut.fel.nss.order.controller;

import cz.cvut.fel.nss.order.dto.CheckoutRequest;
import cz.cvut.fel.nss.order.dto.ProductDto;
import cz.cvut.fel.nss.order.exception.InsufficientStockException;
import cz.cvut.fel.nss.order.exception.ProductNotFoundException;
import cz.cvut.fel.nss.order.exception.ValidationException;
import cz.cvut.fel.nss.order.model.User;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/basket")
@RequiredArgsConstructor
public class BasketController {

    private final HazelcastInstance hazelcastInstance;

    private IMap<String, List<CheckoutRequest.CartItem>> getBasketMap() {
        return hazelcastInstance.getMap("baskets");
    }

    private IMap<Long, ProductDto> getProductMap() {
        return hazelcastInstance.getMap("products");
    }

    @PostMapping("/add")
    public List<CheckoutRequest.CartItem> addToBasket(
            @AuthenticationPrincipal User user,
            @RequestBody CheckoutRequest.CartItem item
    ) {
        if (item.getQuantity() <= 0) {
            throw new ValidationException("Quantity must be greater than zero");
        }

        String username = user.getUsername();
        
        // Validate product existence and fetch current price
        ProductDto product = getProductMap().get(item.getProductId());
        if (product == null) {
            throw new ProductNotFoundException("Product not found: " + item.getProductId());
        }

        // Validate stock availability
        if (product.getStockQuantity() < item.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName() + " (Available: " + product.getStockQuantity() + ", Requested: " + item.getQuantity() + ")");
        }
        
        item.setCurrentPrice(product.getPrice());

        IMap<String, List<CheckoutRequest.CartItem>> baskets = getBasketMap();
        List<CheckoutRequest.CartItem> basket = baskets.getOrDefault(username, new ArrayList<>());
        basket.add(item);
        baskets.put(username, basket);
        return basket;
    }

    @GetMapping
    public List<CheckoutRequest.CartItem> getBasket(@AuthenticationPrincipal User user) {
        return getBasketMap().getOrDefault(user.getUsername(), new ArrayList<>());
    }

    @DeleteMapping
    public void clearBasket(@AuthenticationPrincipal User user) {
        getBasketMap().remove(user.getUsername());
    }
}
