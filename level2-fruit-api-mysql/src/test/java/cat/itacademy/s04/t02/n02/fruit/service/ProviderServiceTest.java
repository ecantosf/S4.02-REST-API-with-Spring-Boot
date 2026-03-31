package cat.itacademy.s04.t02.n02.fruit.service;

import cat.itacademy.s04.t02.n02.fruit.dto.request.ProviderRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.response.ProviderResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.exception.BusinessException;
import cat.itacademy.s04.t02.n02.fruit.exception.DuplicateResourceException;
import cat.itacademy.s04.t02.n02.fruit.exception.ResourceNotFoundException;
import cat.itacademy.s04.t02.n02.fruit.model.Fruit;
import cat.itacademy.s04.t02.n02.fruit.model.Provider;
import cat.itacademy.s04.t02.n02.fruit.repository.ProviderRepository;
import cat.itacademy.s04.t02.n02.fruit.service.impl.ProviderServiceImpl;
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
@DisplayName("ProviderService Unit Tests")
class ProviderServiceTest {

    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private ProviderServiceImpl providerService;

    private ProviderRequestDTO validRequest;
    private Provider provider;

    @BeforeEach
    void setUp() {
        validRequest = new ProviderRequestDTO("Fresh Fruits", "Catalonia");
        provider = new Provider();
        provider.setName("Fresh Fruits");
        provider.setCountry("Catalonia");
        provider.setFruits(new ArrayList<>());
    }

    @Test
    @DisplayName("Should create provider successfully")
    void createProvider_ShouldReturnResponseDTO() {
        Provider savedProvider = new Provider(1L, "Fresh Fruits", "Catalonia", new ArrayList<>());
        when(providerRepository.existsByName(validRequest.name())).thenReturn(false);
        when(providerRepository.save(any(Provider.class))).thenReturn(savedProvider);

        ProviderResponseDTO response = providerService.createProvider(validRequest);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Fresh Fruits");
        assertThat(response.country()).isEqualTo("Catalonia");
        verify(providerRepository).save(any(Provider.class));
    }

    @Test
    @DisplayName("Should throw exception when creating duplicate provider")
    void createProvider_WithDuplicateName_ShouldThrowException() {
        when(providerRepository.existsByName(validRequest.name())).thenReturn(true);

        assertThatThrownBy(() -> providerService.createProvider(validRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Provider already exists with name: Fresh Fruits");
    }

    @Test
    @DisplayName("Should return provider by id")
    void getProviderById_WhenExists_ShouldReturnResponse() {
        Provider existingProvider = new Provider(1L, "Fresh Fruits", "Catalonia", new ArrayList<>());
        when(providerRepository.findById(1L)).thenReturn(Optional.of(existingProvider));

        ProviderResponseDTO response = providerService.getProviderById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Fresh Fruits");
        assertThat(response.country()).isEqualTo("Catalonia");
    }

    @Test
    @DisplayName("Should throw exception when provider not found")
    void getProviderById_WhenNotFound_ShouldThrowException() {
        when(providerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> providerService.getProviderById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Provider not found with id: 999");
    }

    @Test
    @DisplayName("Should return all providers")
    void getAllProviders_ShouldReturnList() {
        List<Provider> providers = List.of(
                new Provider(1L, "Fresh Fruits", "Catalonia", new ArrayList<>()),
                new Provider(2L, "Tropical Fruits", "Brazil", new ArrayList<>())
        );
        when(providerRepository.findAll()).thenReturn(providers);

        List<ProviderResponseDTO> responses = providerService.getAllProviders();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).name()).isEqualTo("Fresh Fruits");
        assertThat(responses.get(1).name()).isEqualTo("Tropical Fruits");
    }

    @Test
    @DisplayName("Should update provider successfully")
    void updateProvider_WhenExists_ShouldReturnUpdated() {
        Provider existingProvider = new Provider(1L, "Fresh Fruits", "Catalonia", new ArrayList<>());
        ProviderRequestDTO updateRequest = new ProviderRequestDTO("Fresh Fruits SL", "Catalonia");

        when(providerRepository.findById(1L)).thenReturn(Optional.of(existingProvider));
        when(providerRepository.existsByName(updateRequest.name())).thenReturn(false);
        when(providerRepository.save(any(Provider.class))).thenAnswer(inv -> inv.getArgument(0));

        ProviderResponseDTO response = providerService.updateProvider(1L, updateRequest);

        assertThat(response.name()).isEqualTo("Fresh Fruits SL");
        assertThat(response.country()).isEqualTo("Catalonia");
    }

    @Test
    @DisplayName("Should throw exception when updating with duplicate name")
    void updateProvider_WithDuplicateName_ShouldThrowException() {
        Provider existingProvider = new Provider(1L, "Fresh Fruits", "Catalonia", new ArrayList<>());
        ProviderRequestDTO updateRequest = new ProviderRequestDTO("Tropical Fruits", "Catalonia");

        when(providerRepository.findById(1L)).thenReturn(Optional.of(existingProvider));
        when(providerRepository.existsByName(updateRequest.name())).thenReturn(true);

        assertThatThrownBy(() -> providerService.updateProvider(1L, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Provider already exists with name: Tropical Fruits");
    }

    @Test
    @DisplayName("Should throw exception when deleting provider with fruits")
    void deleteProvider_WithFruits_ShouldThrowException() {
        Provider providerWithFruits = new Provider(1L, "Fresh Fruits", "Catalonia", new ArrayList<>());
        Fruit fruit = new Fruit(1L, "Apple", 2, providerWithFruits);
        providerWithFruits.getFruits().add(fruit);

        when(providerRepository.findById(1L)).thenReturn(Optional.of(providerWithFruits));

        assertThatThrownBy(() -> providerService.deleteProvider(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot delete provider with 1 associated fruits");
    }

    @Test
    @DisplayName("Should delete provider successfully")
    void deleteProvider_WithoutFruits_ShouldDelete() {
        Provider providerWithoutFruits = new Provider(1L, "Fresh Fruits", "Catalonia", new ArrayList<>());
        when(providerRepository.findById(1L)).thenReturn(Optional.of(providerWithoutFruits));
        doNothing().when(providerRepository).delete(providerWithoutFruits);

        providerService.deleteProvider(1L);

        verify(providerRepository).delete(providerWithoutFruits);
    }
}
