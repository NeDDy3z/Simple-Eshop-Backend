package cz.cvut.fel.nss.notification.dto;

import lombok.Data;

@Data
public class OrderEventDto {
    private Long orderId;
    private Long productId;
    private int quantity;
    private String userEmail;
}
