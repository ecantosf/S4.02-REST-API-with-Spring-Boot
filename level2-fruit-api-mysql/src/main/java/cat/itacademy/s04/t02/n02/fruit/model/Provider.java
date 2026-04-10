package cat.itacademy.s04.t02.n02.fruit.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "providers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String country;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fruit> fruits = new ArrayList<>();

    public void addFruit(Fruit fruit) {
        fruits.add(fruit);
        fruit.setProvider(this);
    }

    public void removeFruit(Fruit fruit) {
        fruits.remove(fruit);
        fruit.setProvider(null);
    }
}