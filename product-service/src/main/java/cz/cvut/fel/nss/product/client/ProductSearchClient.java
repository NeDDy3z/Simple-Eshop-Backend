package cz.cvut.fel.nss.product.client;

import cz.cvut.fel.nss.product.model.Product;
import cz.cvut.fel.nss.product.model.ProductDocument;
import cz.cvut.fel.nss.product.repository.ProductSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ProductSearchClient {

    private final ProductSearchRepository searchRepository;

    @Autowired
    public ProductSearchClient(@Autowired(required = false) ProductSearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    // Metoda pro uložení nového produktu do vyhledávače
    public void indexProduct(Product product) {
        if (searchRepository == null) return;
        ProductDocument doc = new ProductDocument();
        doc.setId(product.getId().toString());
        doc.setName(product.getName());
        doc.setCategory(product.getCategory() != null ? product.getCategory().getName() : null);

        searchRepository.save(doc);
    }

    public void removeProductIndex(Long productId) {
        if (searchRepository == null) return;
        searchRepository.deleteById(productId.toString());
    }

    // Metody pro samotné vyhledávání
    public List<ProductDocument> searchByCategory(String category) {
        if (searchRepository == null) return Collections.emptyList();
        return searchRepository.findByCategory(category);
    }

    public List<ProductDocument> searchByName(String keyword) {
        if (searchRepository == null) return Collections.emptyList();
        return searchRepository.findByNameContainingIgnoreCase(keyword);
    }
}
