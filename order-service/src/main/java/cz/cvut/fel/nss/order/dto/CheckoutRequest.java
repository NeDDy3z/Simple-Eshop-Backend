package cz.cvut.fel.nss.order.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CheckoutRequest {
    private String discountType; // "FIXED" or "PERCENTAGE"
    private BigDecimal discountValue;

    @Data
    public static class CartItem {
        private Long productId;
        private int quantity;
        private BigDecimal currentPrice;
    }
}
