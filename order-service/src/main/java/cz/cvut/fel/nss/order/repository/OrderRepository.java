package cz.cvut.fel.nss.order.repository;

import cz.cvut.fel.nss.order.model.Order;
import cz.cvut.fel.nss.order.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
