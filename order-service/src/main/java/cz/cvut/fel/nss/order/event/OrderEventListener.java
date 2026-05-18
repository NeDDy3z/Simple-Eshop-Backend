package cz.cvut.fel.nss.order.event;

import cz.cvut.fel.nss.order.model.Order;

public interface OrderEventListener {
    void onOrderCreated(Order order);
}
