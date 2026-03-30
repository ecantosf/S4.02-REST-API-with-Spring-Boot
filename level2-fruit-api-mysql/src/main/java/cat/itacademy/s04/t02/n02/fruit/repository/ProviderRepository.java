package cat.itacademy.s04.t02.n02.fruit.repository;

import cat.itacademy.s04.t02.n02.fruit.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {

    Optional<Provider> findByName(String name);

    boolean existsByName(String name);

    List<Provider> findByCountry(String country);

    @Query("SELECT p FROM Provider p LEFT JOIN FETCH p.fruits WHERE p.id = :id")
    Optional<Provider> findByIdWithFruits(@Param("id") Long id);

    @Query("SELECT p FROM Provider p WHERE p.country = :country ORDER BY p.name")
    List<Provider> findByCountryOrderByName(@Param("country") String country);
}
