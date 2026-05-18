package cz.cvut.fel.nss.order.event;

import cz.cvut.fel.nss.order.model.Order;
import cz.cvut.fel.nss.order.model.OrderItem;
import cz.cvut.fel.nss.order.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaListener implements OrderEventListener {
    private final KafkaProducerService kafkaProducerService;

    @Override
    public void onOrderCreated(Order order) {
        for (OrderItem item : order.getItems()) {
            kafkaProducerService.sendOrderCreatedEvent(order.getId(), item.getProductId(), item.getQuantity(), order.getUser().getEmail());
        }
    }
}
