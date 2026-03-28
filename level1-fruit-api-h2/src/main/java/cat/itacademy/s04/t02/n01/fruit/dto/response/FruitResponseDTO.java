package cat.itacademy.s04.t02.n01.fruit.dto.response;

import lombok.Builder;

@Builder
public record FruitResponseDTO(
        Long id,
        String name,
        Integer weightInKilos
) {}
