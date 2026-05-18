package cz.cvut.fel.nss.order.event;

import cz.cvut.fel.nss.order.dto.StockStatusEvent;
import cz.cvut.fel.nss.order.model.Order;
import cz.cvut.fel.nss.order.model.OrderStatus;
import cz.cvut.fel.nss.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class StockStatusListener {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "stock-status", groupId = "order-service-group")
    @Transactional
    public void handleStockStatus(StockStatusEvent event) {
        System.out.println("KAFKA: Received stock status for order: " + event.getOrderId() + " - Success: " + event.isSuccess());
        
        Order order = orderRepository.findById(event.getOrderId()).orElse(null);
        if (order != null) {
            if (event.isSuccess()) {
                // In a multi-item order, we might need more complex logic, 
                // but for now let's say it's COMPLETED if successful.
                order.setStatus(OrderStatus.COMPLETED);
            } else {
                order.setStatus(OrderStatus.CANCELLED);
                System.err.println("Order " + event.getOrderId() + " cancelled: " + event.getMessage());
            }
            orderRepository.save(order);
        }
    }
}
