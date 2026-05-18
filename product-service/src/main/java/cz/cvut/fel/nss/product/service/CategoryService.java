package cz.cvut.fel.nss.product.service;
// tvůj balíček

import cz.cvut.fel.nss.product.model.Category;
import cz.cvut.fel.nss.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @CacheEvict(value = "categories", allEntries = true)
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kategorie nenalezena!"));

        categoryRepository.delete(category);
    }

    // Zde dává Hazelcast největší smysl!
    @Cacheable("categories")
    public List<Category> getAllCategories() {
        System.out.println("--- TAHÁM KATEGORIE Z DATABÁZE ---");
        return categoryRepository.findAll();
    }
}