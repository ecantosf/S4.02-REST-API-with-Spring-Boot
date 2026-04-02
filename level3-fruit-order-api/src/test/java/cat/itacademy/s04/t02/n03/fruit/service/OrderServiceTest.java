package cat.itacademy.s04.t02.n03.fruit.service;

import cat.itacademy.s04.t02.n03.fruit.dto.request.OrderItemRequestDTO;
import cat.itacademy.s04.t02.n03.fruit.dto.request.OrderRequestDTO;
import cat.itacademy.s04.t02.n03.fruit.dto.response.OrderResponseDTO;
import cat.itacademy.s04.t02.n03.fruit.exception.BusinessException;
import cat.itacademy.s04.t02.n03.fruit.exception.ResourceNotFoundException;
import cat.itacademy.s04.t02.n03.fruit.model.Order;
import cat.itacademy.s04.t02.n03.fruit.model.OrderItem;
import cat.itacademy.s04.t02.n03.fruit.repository.OrderRepository;
import cat.itacademy.s04.t02.n03.fruit.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Unit Tests")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderRequestDTO validRequest;
    private Order order;
    private LocalDate tomorrow;

    @BeforeEach
    void setUp() {
        tomorrow = LocalDate.now().plusDays(1);
        
        List<OrderItemRequestDTO> items = List.of(
            new OrderItemRequestDTO("Apple", 5),
            new OrderItemRequestDTO("Banana", 3)
        );
        
        validRequest = new OrderRequestDTO(
            "Joan Garcia",
            tomorrow,
            items
        );
        
        List<OrderItem> orderItems = List.of(
            OrderItem.builder().fruitName("Apple").quantityInKilos(5).build(),
            OrderItem.builder().fruitName("Banana").quantityInKilos(3).build()
        );
        
        order = Order.builder()
            .id("1")
            .clientName("Joan Garcia")
            .deliveryDate(tomorrow)
            .items(orderItems)
            .status("PENDING")
            .build();
    }

    @Test
    @DisplayName("Should create order with valid delivery date")
    void createOrder_WithValidDate_ShouldReturnResponseDTO() {
        Order savedOrder = Order.builder()
            .id("123")
            .clientName("Joan Garcia")
            .deliveryDate(tomorrow)
            .items(order.getItems())
            .status("PENDING")
            .build();
        
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        OrderResponseDTO response = orderService.createOrder(validRequest);

        assertThat(response.id()).isEqualTo("123");
        assertThat(response.clientName()).isEqualTo("Joan Garcia");
        assertThat(response.items()).hasSize(2);
        assertThat(response.status()).isEqualTo("PENDING");
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when delivery date is today")
    void createOrder_WithTodayDate_ShouldThrowException() {
        OrderRequestDTO invalidRequest = new OrderRequestDTO(
            "Joan Garcia",
            LocalDate.now(),
            List.of(new OrderItemRequestDTO("Apple", 5))
        );

        assertThatThrownBy(() -> orderService.createOrder(invalidRequest))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Delivery date must be at least tomorrow");
    }

    @Test
    @DisplayName("Should throw exception when delivery date is in the past")
    void createOrder_WithPastDate_ShouldThrowException() {
        OrderRequestDTO invalidRequest = new OrderRequestDTO(
            "Joan Garcia",
            LocalDate.now().minusDays(1),
            List.of(new OrderItemRequestDTO("Apple", 5))
        );

        assertThatThrownBy(() -> orderService.createOrder(invalidRequest))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Delivery date must be at least tomorrow");
    }

    @Test
    @DisplayName("Should return order by id when exists")
    void getOrderById_WhenExists_ShouldReturnResponse() {
        when(orderRepository.findById("1")).thenReturn(Optional.of(order));

        OrderResponseDTO response = orderService.getOrderById("1");

        assertThat(response.id()).isEqualTo("1");
        assertThat(response.clientName()).isEqualTo("Joan Garcia");
        assertThat(response.deliveryDate()).isEqualTo(tomorrow);
    }

    @Test
    @DisplayName("Should throw exception when order not found by id")
    void getOrderById_WhenNotFound_ShouldThrowException() {
        when(orderRepository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById("999"))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Order not found with id: 999");
    }

    @Test
    @DisplayName("Should return all orders")
    void getAllOrders_ShouldReturnList() {
        List<Order> orders = List.of(order);
        when(orderRepository.findAll()).thenReturn(orders);

        List<OrderResponseDTO> responses = orderService.getAllOrders();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).clientName()).isEqualTo("Joan Garcia");
    }

    @Test
    @DisplayName("Should update order successfully")
    void updateOrder_WhenExists_ShouldReturnUpdated() {
        Order existingOrder = order;
        List<OrderItemRequestDTO> newItems = List.of(
            new OrderItemRequestDTO("Apple", 10),
            new OrderItemRequestDTO("Orange", 4)
        );
        OrderRequestDTO updateRequest = new OrderRequestDTO(
            "Joan Garcia Updated",
            tomorrow.plusDays(1),
            newItems
        );
        
        when(orderRepository.findById("1")).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponseDTO response = orderService.updateOrder("1", updateRequest);

        assertThat(response.clientName()).isEqualTo("Joan Garcia Updated");
        assertThat(response.items()).hasSize(2);
        assertThat(response.items().get(0).fruitName()).isEqualTo("Apple");
        assertThat(response.items().get(0).quantityInKilos()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should throw exception when updating with invalid date")
    void updateOrder_WithInvalidDate_ShouldThrowException() {
        Order existingOrder = order;
        OrderRequestDTO invalidRequest = new OrderRequestDTO(
            "Joan Garcia",
            LocalDate.now(),
            List.of(new OrderItemRequestDTO("Apple", 5))
        );
        
        when(orderRepository.findById("1")).thenReturn(Optional.of(existingOrder));

        assertThatThrownBy(() -> orderService.updateOrder("1", invalidRequest))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Delivery date must be at least tomorrow");
    }

    @Test
    @DisplayName("Should delete order successfully")
    void deleteOrder_WhenExists_ShouldDelete() {
        when(orderRepository.existsById("1")).thenReturn(true);
        doNothing().when(orderRepository).deleteById("1");

        orderService.deleteOrder("1");

        verify(orderRepository).deleteById("1");
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent order")
    void deleteOrder_WhenNotExists_ShouldThrowException() {
        when(orderRepository.existsById("999")).thenReturn(false);

        assertThatThrownBy(() -> orderService.deleteOrder("999"))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Order not found with id: 999");
    }
}