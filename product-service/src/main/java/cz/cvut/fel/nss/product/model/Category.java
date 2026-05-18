package cz.cvut.fel.nss.product.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "categories")
@Data // Lombok: vygeneruje gettery, settery, toString atd.
@NoArgsConstructor // Lombok: prázdný konstruktor pro Hibernate
@AllArgsConstructor // Lombok: konstruktor se všemi parametry
@Builder // Náš povinný návrhový vzor Builder!
public class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}
