package cz.cvut.fel.nss.order.dto;

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
public class InvoiceDto {
    private Long orderId;
    private String customerName;
    private LocalDateTime orderDate;
    private List<InvoiceItemDto> items;
    private BigDecimal totalAmount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceItemDto {
        private Long productId;
        private int quantity;
        private BigDecimal frozenPrice;
        private BigDecimal subtotal;
    }
}
