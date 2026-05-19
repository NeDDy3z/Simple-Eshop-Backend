package cz.cvut.fel.nss.product.controller;

import cz.cvut.fel.nss.product.model.Product;
import cz.cvut.fel.nss.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${KAFKACLUSTER_PREFIX:}")
    private String kafkaPrefix;

    // GET request to retrieve all products
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // GET request for a specific product
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    // POST request to create a new product
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    @PostMapping("/test-kafka")
    public String testKafka(@RequestBody cz.cvut.fel.nss.product.dto.OrderEventDto event) {
        // Send a message to the "order-created" topic
        kafkaTemplate.send(kafkaPrefix + "order-created", event);
        return "Message sent to Kafka!";
    }
}
