package cat.itacademy.s04.t02.n01.fruit.service;

import cat.itacademy.s04.t02.n01.fruit.dto.request.FruitRequestDTO;
import cat.itacademy.s04.t02.n01.fruit.dto.response.FruitResponseDTO;
import cat.itacademy.s04.t02.n01.fruit.exception.ResourceNotFoundException;
import cat.itacademy.s04.t02.n01.fruit.model.Fruit;
import cat.itacademy.s04.t02.n01.fruit.repository.FruitRepository;
import cat.itacademy.s04.t02.n01.fruit.service.impl.FruitServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FruitService Unit Tests")
class FruitServiceTest {

    @Mock
    private FruitRepository fruitRepository;

    @InjectMocks
    private FruitServiceImpl fruitService;

    private FruitRequestDTO validRequest;
    private Fruit fruit;

    @BeforeEach
    void setUp() {
        validRequest = FruitRequestDTO.builder()
                .name("Apple")
                .weightInKilos(2)
                .build();

        fruit = new Fruit();
        fruit.setName("Apple");
        fruit.setWeightInKilos(2);
    }

    @Test
    @DisplayName("Should create fruit successfully")
    void createFruit_ShouldReturnResponseDTO() {
        Fruit savedFruit = new Fruit(1L, "Apple", 2);
        when(fruitRepository.save(any(Fruit.class))).thenReturn(savedFruit);

        FruitResponseDTO response = fruitService.createFruit(validRequest);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Apple");
        assertThat(response.weightInKilos()).isEqualTo(2);
        verify(fruitRepository).save(any(Fruit.class));
    }

    @Test
    @DisplayName("Should throw exception when fruit not found")
    void getFruitById_WhenNotFound_ShouldThrowException() {
        when(fruitRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fruitService.getFruitById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Fruit not found with id: 999");
    }

    @Test
    @DisplayName("Should return all fruits")
    void getAllFruits_ShouldReturnList() {
        List<Fruit> fruits = List.of(
                new Fruit(1L, "Apple", 2),
                new Fruit(2L, "Banana", 3)
        );
        when(fruitRepository.findAll()).thenReturn(fruits);

        List<FruitResponseDTO> responses = fruitService.getAllFruits();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).name()).isEqualTo("Apple");
        assertThat(responses.get(1).name()).isEqualTo("Banana");
    }

    @Test
    @DisplayName("Should update fruit successfully")
    void updateFruit_WhenExists_ShouldReturnUpdated() {
        Fruit existingFruit = new Fruit(1L, "Apple", 2);
        FruitRequestDTO updateRequest = FruitRequestDTO.builder()
                .name("Green Apple")
                .weightInKilos(3)
                .build();

        when(fruitRepository.findById(1L)).thenReturn(Optional.of(existingFruit));
        when(fruitRepository.save(any(Fruit.class))).thenAnswer(inv -> inv.getArgument(0));

        FruitResponseDTO response = fruitService.updateFruit(1L, updateRequest);

        assertThat(response.name()).isEqualTo("Green Apple");
        assertThat(response.weightInKilos()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should delete fruit successfully")
    void deleteFruit_WhenExists_ShouldDelete() {
        when(fruitRepository.existsById(1L)).thenReturn(true);
        doNothing().when(fruitRepository).deleteById(1L);

        fruitService.deleteFruit(1L);

        verify(fruitRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent fruit")
    void deleteFruit_WhenNotExists_ShouldThrowException() {
        when(fruitRepository.existsById(999L)).thenReturn(false);
        
        assertThatThrownBy(() -> fruitService.deleteFruit(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Fruit not found with id: 999");
    }
}