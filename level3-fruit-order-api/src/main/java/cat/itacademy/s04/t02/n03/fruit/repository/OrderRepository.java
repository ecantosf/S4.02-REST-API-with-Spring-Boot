package cat.itacademy.s04.t02.n03.fruit.repository;

import cat.itacademy.s04.t02.n03.fruit.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    List<Order> findByClientName(String clientName);

    List<Order> findByClientNameContainingIgnoreCase(String clientName);

    List<Order> findByDeliveryDateBetween(LocalDate start, LocalDate end);

    List<Order> findByDeliveryDateAfter(LocalDate date);

    List<Order> findByStatus(String status);

    @Query("{ 'items.fruit_name': ?0 }")
    List<Order> findOrdersByFruitName(String fruitName);

    @Query("{ 'client_name': { $regex: ?0, $options: 'i' }, 'delivery_date': { $gte: ?1 } }")
    List<Order> findOrdersByClientNamePatternAndDeliveryAfter(String clientNamePattern, LocalDate date);
    
    @Query(value = "{ 'items.fruit_name': ?0 }", count = true)
    long countOrdersByFruitName(String fruitName);
}
