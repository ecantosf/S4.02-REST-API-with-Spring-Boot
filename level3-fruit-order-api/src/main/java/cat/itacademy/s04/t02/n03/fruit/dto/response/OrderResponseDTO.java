package cat.itacademy.s04.t02.n03.fruit.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
    String id,
    String clientName,
    LocalDate deliveryDate,
    List<OrderItemResponseDTO> items,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}