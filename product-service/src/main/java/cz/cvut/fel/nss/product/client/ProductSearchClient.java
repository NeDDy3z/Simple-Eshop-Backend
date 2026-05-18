package cz.cvut.fel.nss.product.client;

import cz.cvut.fel.nss.product.model.Product;
import cz.cvut.fel.nss.product.model.ProductDocument;
import cz.cvut.fel.nss.product.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductSearchClient {

    private final ProductSearchRepository searchRepository;

    // Metoda pro uložení nového produktu do vyhledávače
    public void indexProduct(Product product) {
        ProductDocument doc = new ProductDocument();
        doc.setId(product.getId().toString());
        doc.setName(product.getName());
        doc.setCategory(product.getCategory() != null ? product.getCategory().getName() : null);

        searchRepository.save(doc);
    }

    public void removeProductIndex(Long productId) {
        searchRepository.deleteById(productId.toString());
    }

    // Metody pro samotné vyhledávání
    public List<ProductDocument> searchByCategory(String category) {
        return searchRepository.findByCategory(category);
    }

    public List<ProductDocument> searchByName(String keyword) {
        return searchRepository.findByNameContainingIgnoreCase(keyword);
    }
}
