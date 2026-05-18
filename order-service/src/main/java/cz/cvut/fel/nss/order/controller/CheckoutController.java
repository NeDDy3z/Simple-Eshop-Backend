package cz.cvut.fel.nss.order.controller;

import cz.cvut.fel.nss.order.dto.CheckoutRequest;
import cz.cvut.fel.nss.order.dto.OrderDto;
import cz.cvut.fel.nss.order.facade.CheckoutFacade;
import cz.cvut.fel.nss.order.model.Order;
import cz.cvut.fel.nss.order.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutFacade checkoutFacade;

    @PostMapping("/checkout")
    public ResponseEntity<OrderDto> checkout(
            @AuthenticationPrincipal User user,
            @RequestBody(required = false) CheckoutRequest request
    ) {
        Order order = checkoutFacade.processCheckout(user, request);
        
        OrderDto dto = OrderDto.builder()
                .id(order.getId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .items(order.getItems().stream().map(item -> OrderDto.OrderItemDto.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .priceAtPurchase(item.getPriceAtPurchase())
                        .build()).collect(Collectors.toList()))
                .build();
                
        return ResponseEntity.ok(dto);
    }
}
