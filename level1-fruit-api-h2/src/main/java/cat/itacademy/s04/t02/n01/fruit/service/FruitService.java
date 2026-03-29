package cat.itacademy.s04.t02.n01.fruit.service;

import cat.itacademy.s04.t02.n01.fruit.dto.request.FruitRequestDTO;
import cat.itacademy.s04.t02.n01.fruit.dto.response.FruitResponseDTO;
import java.util.List;

public interface FruitService {
    FruitResponseDTO createFruit(FruitRequestDTO request);
    FruitResponseDTO getFruitById(Long id);
    List<FruitResponseDTO> getAllFruits();
    FruitResponseDTO updateFruit(Long id, FruitRequestDTO request);
    void deleteFruit(Long id);
}
