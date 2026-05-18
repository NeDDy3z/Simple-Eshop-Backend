package cz.cvut.fel.nss.order.service;

import cz.cvut.fel.nss.order.event.OrderEventListener;
import cz.cvut.fel.nss.order.event.Subject;
import cz.cvut.fel.nss.order.model.Order;
import cz.cvut.fel.nss.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements Subject {

    private final OrderRepository orderRepository;
    private final List<OrderEventListener> observers;

    public void createOrder(Order order) {
        orderRepository.save(order);
        notifyObservers(order);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<Order> getOrdersByUser(cz.cvut.fel.nss.order.model.User user) {
        return orderRepository.findByUser(user);
    }

    @Override
    public void addObserver(OrderEventListener observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(OrderEventListener observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Order order) {
        for (OrderEventListener observer : observers) {
            observer.onOrderCreated(order);
        }
    }
}
