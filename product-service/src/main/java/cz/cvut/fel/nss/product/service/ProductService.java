package cz.cvut.fel.nss.product.service;

import cz.cvut.fel.nss.product.client.ProductSearchClient;
import cz.cvut.fel.nss.product.dto.ProductSyncEvent;
import cz.cvut.fel.nss.product.exception.InsufficientStockException;
import cz.cvut.fel.nss.product.exception.ProductNotFoundException;
import cz.cvut.fel.nss.product.exception.ValidationException;
import cz.cvut.fel.nss.product.model.Product;
import cz.cvut.fel.nss.product.model.ProductDocument;
import cz.cvut.fel.nss.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String SYNC_TOPIC = "product-sync";

    private final ProductSearchClient searchClient;

    @Cacheable("products")
    public List<Product> getAllProducts() {
        System.out.println("--- FETCHING DATA FROM DATABASE ---");
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found!"));
    }

    @CacheEvict(value = "products", allEntries = true)
    @Transactional
    public Product createProduct(Product product) {
        validateProduct(product);
        Product savedProduct = productRepository.save(product);
        sendSyncEvent(savedProduct, "CREATE");

        searchClient.indexProduct(savedProduct);
        return savedProduct;
    }

    @CacheEvict(value = "products", allEntries = true)
    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        validateProduct(productDetails);
        Product product = getProductById(id);
        product.setName(productDetails.getName());
        product.setPrice(productDetails.getPrice());
        product.setStockQuantity(productDetails.getStockQuantity());
        product.setCategory(productDetails.getCategory());
        Product updatedProduct = productRepository.save(product);
        sendSyncEvent(updatedProduct, "UPDATE");
        searchClient.indexProduct(updatedProduct);
        return updatedProduct;
    }

    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new ValidationException("Product name cannot be empty");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new ValidationException("Product price cannot be negative");
        }
        if (product.getStockQuantity() < 0) {
            throw new ValidationException("Product stock quantity cannot be negative");
        }
    }

    @CacheEvict(value = "products", allEntries = true)
    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
        searchClient.removeProductIndex(id);
        kafkaTemplate.send(SYNC_TOPIC, ProductSyncEvent.builder()
                .id(id)
                .action("DELETE")
                .build());
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void deductStock(Long productId, int quantity) {
        Product product = getProductById(productId);

        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock available!");
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);
        sendSyncEvent(product, "UPDATE");
    }

    public List<Product> getProductsByCategoryName(String categoryName) {
        return productRepository.findByCategoryName(categoryName);
    }

    private void sendSyncEvent(Product product, String action) {
        ProductSyncEvent event = ProductSyncEvent.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .action(action)
                .build();
        kafkaTemplate.send(SYNC_TOPIC, event);
    }
}