package cat.itacademy.s04.t02.n02.fruit.service;

import cat.itacademy.s04.t02.n02.fruit.dto.request.FruitRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.response.FruitResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.exception.ResourceNotFoundException;
import cat.itacademy.s04.t02.n02.fruit.model.Fruit;
import cat.itacademy.s04.t02.n02.fruit.model.Provider;
import cat.itacademy.s04.t02.n02.fruit.repository.FruitRepository;
import cat.itacademy.s04.t02.n02.fruit.repository.ProviderRepository;
import cat.itacademy.s04.t02.n02.fruit.service.impl.FruitServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
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

    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private FruitServiceImpl fruitService;

    private FruitRequestDTO validRequest;
    private Provider validProvider;
    private Fruit fruit;

    @BeforeEach
    void setUp() {
        validProvider = new Provider(1L, "Fruita Fresca", "Catalonia", new ArrayList<>());
        validRequest = new FruitRequestDTO("Poma", 2, 1L);

        fruit = new Fruit();
        fruit.setName("Poma");
        fruit.setWeightInKilos(2);
        fruit.setProvider(validProvider);
    }

    @Test
    @DisplayName("Should create fruit with valid provider")
    void createFruit_WithValidProvider_ShouldReturnResponseDTO() {
        Fruit savedFruit = new Fruit(1L, "Poma", 2, validProvider);
        when(providerRepository.findById(1L)).thenReturn(Optional.of(validProvider));
        when(fruitRepository.save(any(Fruit.class))).thenReturn(savedFruit);

        FruitResponseDTO response = fruitService.createFruit(validRequest);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Poma");
        assertThat(response.weightInKilos()).isEqualTo(2);
        assertThat(response.provider().id()).isEqualTo(1L);
        assertThat(response.provider().name()).isEqualTo("Fruita Fresca");
        verify(fruitRepository).save(any(Fruit.class));
    }

    @Test
    @DisplayName("Should throw exception when provider not found")
    void createFruit_WithInvalidProvider_ShouldThrowException() {
        when(providerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fruitService.createFruit(new FruitRequestDTO("Poma", 2, 999L)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Provider not found with id: 999");
    }

    @Test
    @DisplayName("Should return fruit by id with provider data")
    void getFruitById_WhenExists_ShouldReturnResponse() {
        Fruit existingFruit = new Fruit(1L, "Poma", 2, validProvider);
        when(fruitRepository.findByIdWithProvider(1L)).thenReturn(Optional.of(existingFruit));

        FruitResponseDTO response = fruitService.getFruitById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Poma");
        assertThat(response.weightInKilos()).isEqualTo(2);
        assertThat(response.provider().id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should throw exception when fruit not found by id")
    void getFruitById_WhenNotFound_ShouldThrowException() {
        // Arrange
        when(fruitRepository.findByIdWithProvider(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> fruitService.getFruitById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Fruit not found with id: 999");
    }

    @Test
    @DisplayName("Should return all fruits with provider data")
    void getAllFruits_ShouldReturnList() {
        List<Fruit> fruits = List.of(
                new Fruit(1L, "Apple", 2, validProvider),
                new Fruit(2L, "Banana", 3, validProvider)
        );
        when(fruitRepository.findAllWithProvider()).thenReturn(fruits);

        List<FruitResponseDTO> responses = fruitService.getAllFruits();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).name()).isEqualTo("Apple");
        assertThat(responses.get(1).name()).isEqualTo("Banana");
        assertThat(responses.get(0).provider().id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should return fruits by provider")
    void getFruitsByProvider_WhenProviderExists_ShouldReturnList() {
        List<Fruit> fruits = List.of(
                new Fruit(1L, "Apple", 2, validProvider),
                new Fruit(2L, "Pear", 3, validProvider)
        );
        when(providerRepository.existsById(1L)).thenReturn(true);
        when(fruitRepository.findByProviderId(1L)).thenReturn(fruits);

        List<FruitResponseDTO> responses = fruitService.getFruitsByProvider(1L);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).name()).isEqualTo("Apple");
        assertThat(responses.get(1).name()).isEqualTo("Pear");
    }

    @Test
    @DisplayName("Should throw exception when getting fruits for non-existent provider")
    void getFruitsByProvider_WhenProviderNotExists_ShouldThrowException() {
        when(providerRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> fruitService.getFruitsByProvider(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Provider not found with id: 999");
    }

    @Test
    @DisplayName("Should update fruit with same provider")
    void updateFruit_WithSameProvider_ShouldUpdateSuccessfully() {
        Fruit existingFruit = new Fruit(1L, "Apple", 2, validProvider);
        FruitRequestDTO updateRequest = new FruitRequestDTO("Green Apple", 3, 1L);

        when(fruitRepository.findById(1L)).thenReturn(Optional.of(existingFruit));
        when(fruitRepository.save(any(Fruit.class))).thenAnswer(inv -> inv.getArgument(0));

        FruitResponseDTO response = fruitService.updateFruit(1L, updateRequest);

        assertThat(response.name()).isEqualTo("Green Apple");
        assertThat(response.weightInKilos()).isEqualTo(3);
        assertThat(response.provider().id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should update fruit with new provider")
    void updateFruit_WithNewProvider_ShouldUpdateSuccessfully() {
        Provider newProvider = new Provider(2L, "Tropical Fruits", "Brazil", new ArrayList<>());
        Fruit existingFruit = new Fruit(1L, "Apple", 2, validProvider);
        FruitRequestDTO updateRequest = new FruitRequestDTO("Apple", 2, 2L);

        when(fruitRepository.findById(1L)).thenReturn(Optional.of(existingFruit));
        when(providerRepository.findById(2L)).thenReturn(Optional.of(newProvider));
        when(fruitRepository.save(any(Fruit.class))).thenAnswer(inv -> inv.getArgument(0));

        FruitResponseDTO response = fruitService.updateFruit(1L, updateRequest);

        assertThat(response.provider().id()).isEqualTo(2L);
        assertThat(response.provider().name()).isEqualTo("Tropical Fruits");
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
