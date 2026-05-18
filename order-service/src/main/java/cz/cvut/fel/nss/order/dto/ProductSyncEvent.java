package cz.cvut.fel.nss.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSyncEvent {
    private Long id;
    private String name;
    private BigDecimal price;
    private int stockQuantity;
    private String action; // CREATE, UPDATE, DELETE
}
