package cz.cvut.fel.nss.product.kafka;

import cz.cvut.fel.nss.product.dto.OrderEventDto;
import cz.cvut.fel.nss.product.dto.StockStatusEvent;
import cz.cvut.fel.nss.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockListener {

    private final ProductService productService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "order-created", groupId = "product-service-group")
    public void handleOrderCreated(OrderEventDto event) {
        System.out.println("KAFKA RECEIVED MESSAGE: Product purchased - ID: " + event.getProductId() + ", quantity: " + event.getQuantity());

        StockStatusEvent statusEvent = StockStatusEvent.builder()
                .orderId(event.getOrderId())
                .productId(event.getProductId())
                .quantity(event.getQuantity())
                .userEmail(event.getUserEmail())
                .build();

        try {
            productService.deductStock(event.getProductId(), event.getQuantity());
            statusEvent.setSuccess(true);
            statusEvent.setMessage("Stock deducted successfully");
            System.out.println("STOCK SUCCESSFULLY DEDUCTED!");
        } catch (Exception e) {
            statusEvent.setSuccess(false);
            statusEvent.setMessage(e.getMessage());
            System.err.println("Error during stock deduction: " + e.getMessage());
        }

        kafkaTemplate.send("stock-status", statusEvent);
    }
}
