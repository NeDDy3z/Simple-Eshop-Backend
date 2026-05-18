package cz.cvut.fel.nss.product;

import cz.cvut.fel.nss.product.model.Category;
import cz.cvut.fel.nss.product.model.Product;
import cz.cvut.fel.nss.product.service.CategoryService;
import cz.cvut.fel.nss.product.service.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
@EnableCaching
public class ProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductApplication.class, args);
	}

	@Bean
	public CommandLineRunner dataLoader(ProductService productService, CategoryService categoryService) {
		return args -> {
			Category electronics = Category.builder().name("Electronics").build();
			Category books = Category.builder().name("Books").build();
			categoryService.createCategory(electronics);
			categoryService.createCategory(books);

			productService.createProduct(Product.builder()
					.name("Laptop")
					.description("High-end gaming laptop")
					.price(new BigDecimal("1200.00"))
					.stockQuantity(10)
					.category(electronics)
					.build());

			productService.createProduct(Product.builder()
					.name("Smartphone")
					.description("Latest model smartphone")
					.price(new BigDecimal("800.00"))
					.stockQuantity(20)
					.category(electronics)
					.build());

			productService.createProduct(Product.builder()
					.name("Java Programming")
					.description("Learn Java from scratch")
					.price(new BigDecimal("45.50"))
					.stockQuantity(50)
					.category(books)
					.build());
		};
	}

}
