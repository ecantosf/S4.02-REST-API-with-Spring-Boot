package cat.itacademy.s04.t02.n02.fruit.service;

import cat.itacademy.s04.t02.n02.fruit.dto.request.ProviderRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.response.ProviderResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.response.ProviderDetailResponseDTO;
import java.util.List;

public interface ProviderService {
    ProviderResponseDTO createProvider(ProviderRequestDTO request);
    List<ProviderResponseDTO> getAllProviders();
    ProviderResponseDTO getProviderById(Long id);
    ProviderDetailResponseDTO getProviderWithFruits(Long id);
    ProviderResponseDTO updateProvider(Long id, ProviderRequestDTO request);
    void deleteProvider(Long id);
}