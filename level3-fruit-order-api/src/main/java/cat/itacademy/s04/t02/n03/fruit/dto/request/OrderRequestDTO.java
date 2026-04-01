package cat.itacademy.s04.t02.n03.fruit.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public record OrderRequestDTO(
    @NotBlank(message = "Client name is required")
    @Size(min = 2, max = 100, message = "Client name must be between 2 and 100 characters")
    String clientName,
    
    @NotNull(message = "Delivery date is required")
    LocalDate deliveryDate,
    
    @NotEmpty(message = "Order must have at least one item")
    @Valid
    List<OrderItemRequestDTO> items
) {}