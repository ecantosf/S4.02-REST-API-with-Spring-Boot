package cat.itacademy.s04.t02.n03.fruit.dto.request;

import jakarta.validation.constraints.*;

public record OrderItemRequestDTO(
    @NotBlank(message = "Fruit name is required")
    @Size(min = 2, max = 50, message = "Fruit name must be between 2 and 50 characters")
    String fruitName,
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    @Min(value = 1, message = "Minimum quantity is 1 kg")
    @Max(value = 1000, message = "Maximum quantity is 1000 kg")
    Integer quantityInKilos
) {}