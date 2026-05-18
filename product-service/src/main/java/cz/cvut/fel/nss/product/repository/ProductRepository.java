package cz.cvut.fel.nss.product.repository;

import cz.cvut.fel.nss.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Tady můžeme později přidat vlastní hledání, např.:
    //List<Product> findByCategoryId(Long categoryId);
    List<Product> findByCategoryName(String categoryName);
}