package cz.cvut.fel.nss.order;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import cz.cvut.fel.nss.order.dto.ProductDto;
import cz.cvut.fel.nss.order.model.Role;
import cz.cvut.fel.nss.order.model.User;
import cz.cvut.fel.nss.order.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@SpringBootApplication
public class OrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderApplication.class, args);
	}

	@Bean
	public CommandLineRunner dataLoader(UserRepository userRepository, HazelcastInstance hazelcastInstance, PasswordEncoder passwordEncoder) {
		return args -> {
			userRepository.save(User.builder()
					.username("testuser")
					.email("test@example.com")
					.passwordHash(passwordEncoder.encode("password"))
					.role(Role.ROLE_CUSTOMER)
					.build());
			
			userRepository.save(User.builder()
					.username("admin")
					.email("admin@example.com")
					.passwordHash(passwordEncoder.encode("password"))
					.role(Role.ROLE_ADMIN)
					.build());

			IMap<Long, ProductDto> products = hazelcastInstance.getMap("products");

			products.put(1L, ProductDto.builder()
					.id(1L)
					.name("Laptop")
					.price(new BigDecimal("1200.00"))
					.stockQuantity(10)
					.build());

			products.put(2L, ProductDto.builder()
					.id(2L)
					.name("Smartphone")
					.price(new BigDecimal("800.00"))
					.stockQuantity(20)
					.build());

			products.put(3L, ProductDto.builder()
					.id(3L)
					.name("Java Programming")
					.price(new BigDecimal("45.50"))
					.stockQuantity(50)
					.build());
		};
	}

}
