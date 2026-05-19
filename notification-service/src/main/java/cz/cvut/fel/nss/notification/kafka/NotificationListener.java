package cz.cvut.fel.nss.notification.kafka;

import cz.cvut.fel.nss.notification.dto.OrderEventDto;
import cz.cvut.fel.nss.notification.service.NotificationObserver;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NotificationListener {

    private final List<NotificationObserver> observers = new ArrayList<>();

    public NotificationListener(List<NotificationObserver> notificationObservers) {
        this.observers.addAll(notificationObservers);
    }

    @KafkaListener(topics = "${KAFKACLUSTER_PREFIX:}order-created", groupId = "notification-group")
    public void handleOrderCreated(OrderEventDto event) {
        System.out.println("KAFKA: Přijata zpráva o objednávce.");
        notifyObservers(event);
    }

    private void notifyObservers(OrderEventDto event) {
        observers.forEach(observer -> observer.onOrderCreated(event));
    }
}
