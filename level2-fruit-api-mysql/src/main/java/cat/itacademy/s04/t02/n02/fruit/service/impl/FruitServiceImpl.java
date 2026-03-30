package cat.itacademy.s04.t02.n02.fruit.service.impl;

import cat.itacademy.s04.t02.n02.fruit.dto.request.FruitRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.response.*;
import cat.itacademy.s04.t02.n02.fruit.exception.ResourceNotFoundException;
import cat.itacademy.s04.t02.n02.fruit.model.Fruit;
import cat.itacademy.s04.t02.n02.fruit.model.Provider;
import cat.itacademy.s04.t02.n02.fruit.repository.FruitRepository;
import cat.itacademy.s04.t02.n02.fruit.repository.ProviderRepository;
import cat.itacademy.s04.t02.n02.fruit.service.FruitService;
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
    private final ProviderRepository providerRepository;

    @Override
    public FruitResponseDTO createFruit(FruitRequestDTO request) {
        // Validate that the provider exists
        Provider provider = providerRepository.findById(request.providerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Provider not found with id: " + request.providerId()
                ));

        Fruit fruit = new Fruit();
        fruit.setName(request.name());
        fruit.setWeightInKilos(request.weightInKilos());
        fruit.setProvider(provider);

        Fruit saved = fruitRepository.save(fruit);

        return mapToResponse(saved);
    }

    @Override
    public FruitResponseDTO getFruitById(Long id) {
        Fruit fruit = fruitRepository.findByIdWithProvider(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Fruit not found with id: " + id
                ));

        return mapToResponse(fruit);
    }

    @Override
    public List<FruitResponseDTO> getAllFruits() {
        return fruitRepository.findAllWithProvider()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FruitResponseDTO> getFruitsByProvider(Long providerId) {
        // Verify provider exists
        if (!providerRepository.existsById(providerId)) {
            throw new ResourceNotFoundException(
                    "Provider not found with id: " + providerId
            );
        }

        return fruitRepository.findByProviderId(providerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FruitResponseDTO updateFruit(Long id, FruitRequestDTO request) {
        Fruit fruit = fruitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Fruit not found with id: " + id
                ));

        // If provider is changing, validate the new provider exists
        if (!fruit.getProvider().getId().equals(request.providerId())) {
            Provider newProvider = providerRepository.findById(request.providerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Provider not found with id: " + request.providerId()
                    ));
            fruit.setProvider(newProvider);
        }

        fruit.setName(request.name());
        fruit.setWeightInKilos(request.weightInKilos());

        Fruit updated = fruitRepository.save(fruit);

        return mapToResponse(updated);
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

    private FruitResponseDTO mapToResponse(Fruit fruit) {
        return new FruitResponseDTO(
                fruit.getId(),
                fruit.getName(),
                fruit.getWeightInKilos(),
                new ProviderSummaryDTO(
                        fruit.getProvider().getId(),
                        fruit.getProvider().getName(),
                        fruit.getProvider().getCountry()
                )
        );
    }
}
