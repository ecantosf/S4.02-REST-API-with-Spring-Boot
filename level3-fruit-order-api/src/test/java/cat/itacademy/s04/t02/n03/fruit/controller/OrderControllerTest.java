package cat.itacademy.s04.t02.n03.fruit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cat.itacademy.s04.t02.n03.fruit.dto.request.OrderItemRequestDTO;
import cat.itacademy.s04.t02.n03.fruit.dto.request.OrderRequestDTO;
import cat.itacademy.s04.t02.n03.fruit.model.Order;
import cat.itacademy.s04.t02.n03.fruit.model.OrderItem;
import cat.itacademy.s04.t02.n03.fruit.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("OrderController Integration Tests")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private LocalDate tomorrow;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        tomorrow = LocalDate.now().plusDays(1);
        today = LocalDate.now();
    }

    @Test
    @DisplayName("POST /orders - Should create order and return 201")
    void createOrder_WithValidData_ShouldReturnCreated() throws Exception {
        OrderRequestDTO request = new OrderRequestDTO(
                "Joan Garcia",
                tomorrow,
                List.of(
                        new OrderItemRequestDTO("Apple", 5),
                        new OrderItemRequestDTO("Banana", 3)
                )
        );

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clientName").value("Joan Garcia"))
                .andExpect(jsonPath("$.deliveryDate").value(tomorrow.toString()))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].fruitName").value("Apple"))
                .andExpect(jsonPath("$.items[0].quantityInKilos").value(5))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("POST /orders - With delivery date today should return 400")
    void createOrder_WithTodayDate_ShouldReturnBadRequest() throws Exception {
        OrderRequestDTO request = new OrderRequestDTO(
                "Joan Garcia",
                today,
                List.of(new OrderItemRequestDTO("Apple", 5))
        );

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Delivery date must be at least tomorrow")));
    }

    @Test
    @DisplayName("POST /orders - With empty items list should return 400")
    void createOrder_WithEmptyItems_ShouldReturnBadRequest() throws Exception {
        OrderRequestDTO request = new OrderRequestDTO(
                "Joan Garcia",
                tomorrow,
                List.of()
        );

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.items").value("Order must have at least one item"));
    }

    @Test
    @DisplayName("POST /orders - With invalid quantity should return 400")
    void createOrder_WithInvalidQuantity_ShouldReturnBadRequest() throws Exception {
        OrderRequestDTO request = new OrderRequestDTO(
                "Joan Garcia",
                tomorrow,
                List.of(new OrderItemRequestDTO("Apple", 0))
        );

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors['items[0].quantityInKilos']").exists());
    }

    @Test
    @DisplayName("GET /orders - Should return all orders")
    void getAllOrders_ShouldReturnList() throws Exception {
        // Create test orders
        Order order1 = Order.builder()
                .clientName("Joan Garcia")
                .deliveryDate(tomorrow)
                .items(List.of(OrderItem.builder().fruitName("Apple").quantityInKilos(5).build()))
                .build();

        Order order2 = Order.builder()
                .clientName("Maria Lopez")
                .deliveryDate(tomorrow.plusDays(1))
                .items(List.of(OrderItem.builder().fruitName("Banana").quantityInKilos(3).build()))
                .build();

        orderRepository.saveAll(List.of(order1, order2));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].clientName").value("Joan Garcia"))
                .andExpect(jsonPath("$[1].clientName").value("Maria Lopez"));
    }

    @Test
    @DisplayName("GET /orders/{id} - When exists should return 200")
    void getOrderById_WhenExists_ShouldReturnOk() throws Exception {
        Order saved = orderRepository.save(Order.builder()
                .clientName("Joan Garcia")
                .deliveryDate(tomorrow)
                .items(List.of(OrderItem.builder().fruitName("Apple").quantityInKilos(5).build()))
                .build());

        mockMvc.perform(get("/orders/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientName").value("Joan Garcia"))
                .andExpect(jsonPath("$.id").value(saved.getId()));
    }

    @Test
    @DisplayName("GET /orders/{id} - When not exists should return 404")
    void getOrderById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/orders/507f1f77bcf86cd799439011"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Order not found")));
    }

    @Test
    @DisplayName("PUT /orders/{id} - Should update order and return 200")
    void updateOrder_WhenExists_ShouldReturnOk() throws Exception {
        Order saved = orderRepository.save(Order.builder()
                .clientName("Joan Garcia")
                .deliveryDate(tomorrow)
                .items(List.of(OrderItem.builder().fruitName("Apple").quantityInKilos(5).build()))
                .build());

        OrderRequestDTO updateRequest = new OrderRequestDTO(
                "Joan Garcia Updated",
                tomorrow.plusDays(2),
                List.of(
                        new OrderItemRequestDTO("Apple", 10),
                        new OrderItemRequestDTO("Orange", 4)
                )
        );

        mockMvc.perform(put("/orders/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientName").value("Joan Garcia Updated"))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].quantityInKilos").value(10));
    }

    @Test
    @DisplayName("PUT /orders/{id} - With past date should return 400")
    void updateOrder_WithPastDate_ShouldReturnBadRequest() throws Exception {
        Order saved = orderRepository.save(Order.builder()
                .clientName("Joan Garcia")
                .deliveryDate(tomorrow)
                .items(List.of(OrderItem.builder().fruitName("Apple").quantityInKilos(5).build()))
                .build());

        OrderRequestDTO updateRequest = new OrderRequestDTO(
                "Joan Garcia",
                today,
                List.of(new OrderItemRequestDTO("Apple", 5))
        );

        mockMvc.perform(put("/orders/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Delivery date must be at least tomorrow")));
    }

    @Test
    @DisplayName("DELETE /orders/{id} - Should delete order and return 204")
    void deleteOrder_WhenExists_ShouldReturnNoContent() throws Exception {
        Order saved = orderRepository.save(Order.builder()
                .clientName("Joan Garcia")
                .deliveryDate(tomorrow)
                .items(List.of(OrderItem.builder().fruitName("Apple").quantityInKilos(5).build()))
                .build());

        mockMvc.perform(delete("/orders/" + saved.getId()))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/orders/" + saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /orders/{id} - When not exists should return 404")
    void deleteOrder_WhenNotExists_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/orders/507f1f77bcf86cd799439011"))
                .andExpect(status().isNotFound());
    }
}