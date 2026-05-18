package cz.cvut.fel.nss.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockStatusEvent {
    private Long orderId;
    private Long productId;
    private int quantity;
    private String userEmail;
    private boolean success;
    private String message;
}
