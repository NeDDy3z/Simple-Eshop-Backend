package cz.cvut.fel.nss.product.controller;

import cz.cvut.fel.nss.product.client.ProductSearchClient;
import cz.cvut.fel.nss.product.model.ProductDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/search") // Samostatná URL cesta pro hledání
@RequiredArgsConstructor
public class ProductSearchController {

    private final ProductSearchClient searchClient;

    // Fulltext search GET /api/products/search?query=notebook
    @GetMapping
    public List<ProductDocument> searchByName(@RequestParam String query) {
        return searchClient.searchByName(query);
    }

    // Fileter by category GET /api/products/search/category/Elektronika
    @GetMapping("/category/{category}")
    public List<ProductDocument> searchByCategory(@PathVariable String category) {
        return searchClient.searchByCategory(category);
    }
}