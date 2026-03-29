package cat.itacademy.s04.t02.n01.fruit.service.impl;

import cat.itacademy.s04.t02.n01.fruit.dto.request.FruitRequestDTO;
import cat.itacademy.s04.t02.n01.fruit.dto.response.FruitResponseDTO;
import cat.itacademy.s04.t02.n01.fruit.exception.ResourceNotFoundException;
import cat.itacademy.s04.t02.n01.fruit.model.Fruit;
import cat.itacademy.s04.t02.n01.fruit.repository.FruitRepository;
import cat.itacademy.s04.t02.n01.fruit.service.FruitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FruitServiceImpl implements FruitService {

    private final FruitRepository fruitRepository;

    @Override
    public FruitResponseDTO createFruit(FruitRequestDTO request) {
        Fruit fruit = new Fruit();
        fruit.setName(request.name());
        fruit.setWeightInKilos(request.weightInKilos());

        Fruit saved = fruitRepository.save(fruit);

        return FruitResponseDTO.builder()
                .id(saved.getId())
                .name(saved.getName())
                .weightInKilos(saved.getWeightInKilos())
                .build();
    }

    @Override
    public FruitResponseDTO getFruitById(Long id) {
        Fruit fruit = fruitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Fruit not found with id: " + id
                ));

        return FruitResponseDTO.builder()
                .id(fruit.getId())
                .name(fruit.getName())
                .weightInKilos(fruit.getWeightInKilos())
                .build();
    }

    @Override
    public List<FruitResponseDTO> getAllFruits() {
        return fruitRepository.findAll()
                .stream()
                .map(fruit -> FruitResponseDTO.builder()
                        .id(fruit.getId())
                        .name(fruit.getName())
                        .weightInKilos(fruit.getWeightInKilos())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public FruitResponseDTO updateFruit(Long id, FruitRequestDTO request) {
        Fruit fruit = fruitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Fruit not found with id: " + id
                ));

        fruit.setName(request.name());
        fruit.setWeightInKilos(request.weightInKilos());

        Fruit updated = fruitRepository.save(fruit);

        return FruitResponseDTO.builder()
                .id(updated.getId())
                .name(updated.getName())
                .weightInKilos(updated.getWeightInKilos())
                .build();
    }

    @Override
    public void deleteFruit(Long id) {
        if (!fruitRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Fruit not found with id: " + id
            );
        }
        fruitRepository.deleteById(id);
    }
}
