package cat.itacademy.s04.t02.n02.fruit.dto.response;

import java.util.List;

public record ProviderDetailResponseDTO(
        Long id,
        String name,
        String country,
        List<FruitSummaryDTO> fruits
) {}
