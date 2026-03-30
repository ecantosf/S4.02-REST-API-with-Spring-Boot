package cat.itacademy.s04.t02.n02.fruit.dto.request;

import jakarta.validation.constraints.*;

public record ProviderRequestDTO(
        @NotBlank(message = "Provider name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @NotBlank(message = "Country is required")
        @Size(min = 2, max = 50, message = "Country must be between 2 and 50 characters")
        String country
) {}