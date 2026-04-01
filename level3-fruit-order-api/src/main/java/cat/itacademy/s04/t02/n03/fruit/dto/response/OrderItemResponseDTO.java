package cat.itacademy.s04.t02.n03.fruit.dto.response;

public record OrderItemResponseDTO(
    String fruitName,
    Integer quantityInKilos
) {}