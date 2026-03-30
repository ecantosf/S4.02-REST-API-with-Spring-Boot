package cat.itacademy.s04.t02.n02.fruit.dto.response;

public record FruitResponseDTO(
        Long id,
        String name,
        Integer weightInKilos,
        ProviderSummaryDTO provider
) {}
