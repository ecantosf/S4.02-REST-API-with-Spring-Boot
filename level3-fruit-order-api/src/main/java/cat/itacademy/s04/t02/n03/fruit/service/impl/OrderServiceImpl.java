package cat.itacademy.s04.t02.n03.fruit.service.impl;

import cat.itacademy.s04.t02.n03.fruit.dto.request.OrderItemRequestDTO;
import cat.itacademy.s04.t02.n03.fruit.dto.request.OrderRequestDTO;
import cat.itacademy.s04.t02.n03.fruit.dto.response.OrderItemResponseDTO;
import cat.itacademy.s04.t02.n03.fruit.dto.response.OrderResponseDTO;
import cat.itacademy.s04.t02.n03.fruit.exception.ResourceNotFoundException;
import cat.itacademy.s04.t02.n03.fruit.model.Order;
import cat.itacademy.s04.t02.n03.fruit.model.OrderItem;
import cat.itacademy.s04.t02.n03.fruit.repository.OrderRepository;
import cat.itacademy.s04.t02.n03.fruit.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        // Convert DTO to entity
        Order order = Order.builder()
                .clientName(request.clientName())
                .deliveryDate(request.deliveryDate())
                .items(request.items().stream()
                        .map(this::mapToOrderItem)
                        .collect(Collectors.toList()))
                .status("PENDING")
                .build();

        Order saved = orderRepository.save(order);
        return mapToResponse(saved);
    }

    @Override
    public OrderResponseDTO getOrderById(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + id
                ));
        return mapToResponse(order);
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO updateOrder(String id, OrderRequestDTO request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + id
                ));

        // Update fields
        order.setClientName(request.clientName());
        order.setDeliveryDate(request.deliveryDate());
        order.setItems(request.items().stream()
                .map(this::mapToOrderItem)
                .collect(Collectors.toList()));

        Order updated = orderRepository.save(order);
        return mapToResponse(updated);
    }

    @Override
    public void deleteOrder(String id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Order not found with id: " + id
            );
        }
        orderRepository.deleteById(id);
    }

    // Helper mapping methods
    private OrderItem mapToOrderItem(OrderItemRequestDTO dto) {
        return OrderItem.builder()
                .fruitName(dto.fruitName())
                .quantityInKilos(dto.quantityInKilos())
                .build();
    }

    private OrderResponseDTO mapToResponse(Order order) {
        List<OrderItemResponseDTO> items = order.getItems()
                .stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getFruitName(),
                        item.getQuantityInKilos()
                ))
                .collect(Collectors.toList());

        return new OrderResponseDTO(
                order.getId(),
                order.getClientName(),
                order.getDeliveryDate(),
                items,
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}