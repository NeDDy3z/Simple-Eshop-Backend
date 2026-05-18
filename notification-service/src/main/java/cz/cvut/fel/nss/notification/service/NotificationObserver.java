package cz.cvut.fel.nss.notification.service;

import cz.cvut.fel.nss.notification.dto.OrderEventDto;

public interface NotificationObserver {
    void onOrderCreated(OrderEventDto event);
}
