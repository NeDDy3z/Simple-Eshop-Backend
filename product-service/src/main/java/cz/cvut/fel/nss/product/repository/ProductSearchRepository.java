package cz.cvut.fel.nss.product.repository;

import cz.cvut.fel.nss.product.model.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {
    List<ProductDocument> findByCategory(String category);
    List<ProductDocument> findByNameContainingIgnoreCase(String name);
}