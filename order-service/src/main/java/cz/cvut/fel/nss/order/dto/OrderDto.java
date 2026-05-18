package cz.cvut.fel.nss.order.dto;

import cz.cvut.fel.nss.order.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private List<OrderItemDto> items;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime orderDate;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDto {
        private Long productId;
        private int quantity;
        private BigDecimal priceAtPurchase;
    }
}
