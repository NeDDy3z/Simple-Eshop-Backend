package cz.cvut.fel.nss.order.controller;

import cz.cvut.fel.nss.order.dto.InvoiceDto;
import cz.cvut.fel.nss.order.dto.OrderDto;
import cz.cvut.fel.nss.order.model.Order;
import cz.cvut.fel.nss.order.model.User;
import cz.cvut.fel.nss.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderDto> getMyOrders(@AuthenticationPrincipal User user) {
        return orderService.getOrdersByUser(user).stream()
                .map(order -> OrderDto.builder()
                        .id(order.getId())
                        .totalAmount(order.getTotalAmount())
                        .status(order.getStatus())
                        .orderDate(order.getOrderDate())
                        .items(order.getItems().stream().map(item -> OrderDto.OrderItemDto.builder()
                                .productId(item.getProductId())
                                .quantity(item.getQuantity())
                                .priceAtPurchase(item.getPriceAtPurchase())
                                .build()).collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    @GetMapping("/{orderId}/invoice")
    public ResponseEntity<InvoiceDto> getInvoice(@AuthenticationPrincipal User user, @PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        
        // Security check: ensure the order belongs to the user
        if (!order.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        InvoiceDto invoice = InvoiceDto.builder()
                .orderId(order.getId())
                .customerName(order.getUser().getUsername())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .items(order.getItems().stream().map(item -> InvoiceDto.InvoiceItemDto.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .frozenPrice(item.getPriceAtPurchase())
                        .subtotal(item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build()).collect(Collectors.toList()))
                .build();

        return ResponseEntity.ok(invoice);
    }
}
