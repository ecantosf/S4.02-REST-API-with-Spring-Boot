package cat.itacademy.s04.t02.n02.fruit.repository;

import cat.itacademy.s04.t02.n02.fruit.model.Fruit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FruitRepository extends JpaRepository<Fruit, Long> {

    List<Fruit> findByProviderId(Long providerId);

    List<Fruit> findByNameContainingIgnoreCase(String name);

    @Query("SELECT f FROM Fruit f JOIN FETCH f.provider WHERE f.id = :id")
    Optional<Fruit> findByIdWithProvider(@Param("id") Long id);

    @Query("SELECT f FROM Fruit f JOIN FETCH f.provider")
    List<Fruit> findAllWithProvider();

    @Query("SELECT f FROM Fruit f WHERE f.provider.id = :providerId AND f.weightInKilos > :minWeight")
    List<Fruit> findHeavyFruitsByProvider(@Param("providerId") Long providerId, @Param("minWeight") int minWeight);
}
