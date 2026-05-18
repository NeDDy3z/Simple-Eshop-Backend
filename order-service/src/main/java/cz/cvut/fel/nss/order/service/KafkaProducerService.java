package cz.cvut.fel.nss.order.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "order-created";

    public void sendOrderCreatedEvent(Long orderId, Long productId, int quantity, String userEmail) {
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, productId, quantity, userEmail);
        kafkaTemplate.send(TOPIC, event);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrderCreatedEvent {
        private Long orderId;
        private Long productId;
        private int quantity;
        private String userEmail;
    }
}
