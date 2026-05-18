package cz.cvut.fel.nss.order.event;

import cz.cvut.fel.nss.order.model.Order;

public interface Subject {
    void addObserver(OrderEventListener observer);
    void removeObserver(OrderEventListener observer);
    void notifyObservers(Order order);
}
