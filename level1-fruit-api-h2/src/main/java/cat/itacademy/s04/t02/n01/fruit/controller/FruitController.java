package cat.itacademy.s04.t02.n01.fruit.controller;

import cat.itacademy.s04.t02.n01.fruit.dto.request.FruitRequestDTO;
import cat.itacademy.s04.t02.n01.fruit.dto.response.FruitResponseDTO;
import cat.itacademy.s04.t02.n01.fruit.service.FruitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fruits")
@RequiredArgsConstructor
public class FruitController {

    private final FruitService fruitService;

    @PostMapping
    public ResponseEntity<FruitResponseDTO> createFruit(
            @Valid @RequestBody FruitRequestDTO request) {
        FruitResponseDTO created = fruitService.createFruit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<FruitResponseDTO>> getAllFruits() {
        return ResponseEntity.ok(fruitService.getAllFruits());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FruitResponseDTO> getFruitById(
            @PathVariable Long id) {
        return ResponseEntity.ok(fruitService.getFruitById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FruitResponseDTO> updateFruit(
            @PathVariable Long id,
            @Valid @RequestBody FruitRequestDTO request) {
        return ResponseEntity.ok(fruitService.updateFruit(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFruit(@PathVariable Long id) {
        fruitService.deleteFruit(id);
        return ResponseEntity.noContent().build();
    }
}