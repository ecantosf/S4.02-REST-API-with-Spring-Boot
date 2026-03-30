package cat.itacademy.s04.t02.n02.fruit.service.impl;

import cat.itacademy.s04.t02.n02.fruit.dto.request.ProviderRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.response.*;
import cat.itacademy.s04.t02.n02.fruit.exception.*;
import cat.itacademy.s04.t02.n02.fruit.model.Provider;
import cat.itacademy.s04.t02.n02.fruit.repository.ProviderRepository;
import cat.itacademy.s04.t02.n02.fruit.service.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;

    @Override
    public ProviderResponseDTO createProvider(ProviderRequestDTO request) {
        // Check for duplicate name
        if (providerRepository.existsByName(request.name())) {
            throw new DuplicateResourceException(
                    "Provider already exists with name: " + request.name()
            );
        }

        Provider provider = new Provider();
        provider.setName(request.name());
        provider.setCountry(request.country());

        Provider saved = providerRepository.save(provider);

        return new ProviderResponseDTO(
                saved.getId(),
                saved.getName(),
                saved.getCountry()
        );
    }

    @Override
    public List<ProviderResponseDTO> getAllProviders() {
        return providerRepository.findAll()
                .stream()
                .map(p -> new ProviderResponseDTO(p.getId(), p.getName(), p.getCountry()))
                .collect(Collectors.toList());
    }

    @Override
    public ProviderResponseDTO getProviderById(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Provider not found with id: " + id
                ));

        return new ProviderResponseDTO(
                provider.getId(),
                provider.getName(),
                provider.getCountry()
        );
    }

    @Override
    public ProviderDetailResponseDTO getProviderWithFruits(Long id) {
        Provider provider = providerRepository.findByIdWithFruits(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Provider not found with id: " + id
                ));

        List<FruitSummaryDTO> fruits = provider.getFruits()
                .stream()
                .map(f -> new FruitSummaryDTO(f.getId(), f.getName(), f.getWeightInKilos()))
                .collect(Collectors.toList());

        return new ProviderDetailResponseDTO(
                provider.getId(),
                provider.getName(),
                provider.getCountry(),
                fruits
        );
    }

    @Override
    public ProviderResponseDTO updateProvider(Long id, ProviderRequestDTO request) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Provider not found with id: " + id
                ));

        if (!provider.getName().equals(request.name()) &&
                providerRepository.existsByName(request.name())) {
            throw new DuplicateResourceException(
                    "Provider already exists with name: " + request.name()
            );
        }

        provider.setName(request.name());
        provider.setCountry(request.country());

        Provider updated = providerRepository.save(provider);

        return new ProviderResponseDTO(
                updated.getId(),
                updated.getName(),
                updated.getCountry()
        );
    }

    @Override
    public void deleteProvider(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Provider not found with id: " + id
                ));

        if (!provider.getFruits().isEmpty()) {
            throw new BusinessException(
                    "Cannot delete provider with " + provider.getFruits().size() +
                            " associated fruits. Please delete or reassign fruits first."
            );
        }

        providerRepository.delete(provider);
    }
}
