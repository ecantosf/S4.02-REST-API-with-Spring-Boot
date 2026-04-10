package cat.itacademy.s04.t02.n02.fruit.controller;

import cat.itacademy.s04.t02.n02.fruit.dto.request.ProviderRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.response.ProviderResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.dto.response.ProviderDetailResponseDTO;
import cat.itacademy.s04.t02.n02.fruit.service.ProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;

    @PostMapping
    public ResponseEntity<ProviderResponseDTO> createProvider(
            @Valid @RequestBody ProviderRequestDTO request) {
        ProviderResponseDTO created = providerService.createProvider(request);
        return ResponseEntity
                .created(URI.create("/providers/" + created.id()))
                .body(created);
    }

    @GetMapping
    public ResponseEntity<List<ProviderResponseDTO>> getAllProviders() {
        return ResponseEntity.ok(providerService.getAllProviders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProviderResponseDTO> getProviderById(
            @PathVariable Long id) {
        return ResponseEntity.ok(providerService.getProviderById(id));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<ProviderDetailResponseDTO> getProviderWithFruits(
            @PathVariable Long id) {
        return ResponseEntity.ok(providerService.getProviderWithFruits(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProviderResponseDTO> updateProvider(
            @PathVariable Long id,
            @Valid @RequestBody ProviderRequestDTO request) {
        return ResponseEntity.ok(providerService.updateProvider(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvider(@PathVariable Long id) {
        providerService.deleteProvider(id);
        return ResponseEntity.noContent().build();
    }
}
