package cat.itacademy.s04.t02.n03.fruit.service;

import cat.itacademy.s04.t02.n03.fruit.dto.request.OrderRequestDTO;
import cat.itacademy.s04.t02.n03.fruit.dto.response.OrderResponseDTO;
import java.util.List;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO request);
    OrderResponseDTO getOrderById(String id);
    List<OrderResponseDTO> getAllOrders();
    OrderResponseDTO updateOrder(String id, OrderRequestDTO request);
    void deleteOrder(String id);
}