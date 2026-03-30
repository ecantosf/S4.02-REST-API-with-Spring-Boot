package cat.itacademy.s04.t02.n02.fruit.dto.request;

import jakarta.validation.constraints.*;

public record FruitRequestDTO(
        @NotBlank(message = "Fruit name is required")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        String name,

        @NotNull(message = "Weight is required")
        @Positive(message = "Weight must be positive")
        @Max(value = 1000, message = "Weight cannot exceed 1000 kg")
        Integer weightInKilos,

        @NotNull(message = "Provider ID is required")
        @Positive(message = "Provider ID must be positive")
        Long providerId
) {}