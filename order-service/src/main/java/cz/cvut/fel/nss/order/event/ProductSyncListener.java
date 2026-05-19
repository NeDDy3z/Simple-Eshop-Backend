package cz.cvut.fel.nss.order.event;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import cz.cvut.fel.nss.order.dto.ProductDto;
import cz.cvut.fel.nss.order.dto.ProductSyncEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductSyncListener {

    private final HazelcastInstance hazelcastInstance;

    @KafkaListener(topics = "${KAFKACLUSTER_PREFIX:}product-sync", groupId = "order-service-group")
    public void handleProductSync(ProductSyncEvent event) {
        System.out.println("KAFKA: Received product synchronization event: " + event.getId() + " - " + event.getAction());
        
        IMap<Long, ProductDto> products = hazelcastInstance.getMap("products");

        switch (event.getAction()) {
            case "CREATE", "UPDATE" -> {
                ProductDto product = ProductDto.builder()
                        .id(event.getId())
                        .name(event.getName())
                        .price(event.getPrice())
                        .stockQuantity(event.getStockQuantity())
                        .build();
                products.put(event.getId(), product);
            }
            case "DELETE" -> products.remove(event.getId());
        }
    }
}
