package cat.itacademy.s04.t02.n01.fruit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cat.itacademy.s04.t02.n01.fruit.dto.request.FruitRequestDTO;
import cat.itacademy.s04.t02.n01.fruit.model.Fruit;
import cat.itacademy.s04.t02.n01.fruit.repository.FruitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("FruitController Integration Tests")
class FruitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FruitRepository fruitRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        fruitRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /fruits - Should create fruit and return 201")
    void createFruit_ShouldReturnCreated() throws Exception {
        FruitRequestDTO request = FruitRequestDTO.builder()
                .name("Apple")
                .weightInKilos(2)
                .build();

        mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Apple"))
                .andExpect(jsonPath("$.weightInKilos").value(2))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("POST /fruits - With invalid data should return 400")
    void createFruit_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        FruitRequestDTO invalidRequest = FruitRequestDTO.builder()
                .name("")
                .weightInKilos(-1)
                .build();

        mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /fruits - Should return all fruits")
    void getAllFruits_ShouldReturnList() throws Exception {
        fruitRepository.save(new Fruit(null, "Apple", 2));
        fruitRepository.save(new Fruit(null, "Banana", 3));

        mockMvc.perform(get("/fruits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Apple"))
                .andExpect(jsonPath("$[1].name").value("Banana"));
    }

    @Test
    @DisplayName("GET /fruits/{id} - When exists should return 200")
    void getFruitById_WhenExists_ShouldReturnOk() throws Exception {
        Fruit saved = fruitRepository.save(new Fruit(null, "Apple", 2));

        mockMvc.perform(get("/fruits/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Apple"))
                .andExpect(jsonPath("$.weightInKilos").value(2));
    }

    @Test
    @DisplayName("GET /fruits/{id} - When not exists should return 404")
    void getFruitById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/fruits/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("PUT /fruits/{id} - Should update and return 200")
    void updateFruit_WhenExists_ShouldReturnOk() throws Exception {
        Fruit saved = fruitRepository.save(new Fruit(null, "Apple", 2));

        FruitRequestDTO updateRequest = FruitRequestDTO.builder()
                .name("Green Apple")
                .weightInKilos(3)
                .build();

        mockMvc.perform(put("/fruits/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Green Apple"))
                .andExpect(jsonPath("$.weightInKilos").value(3));
    }

    @Test
    @DisplayName("DELETE /fruits/{id} - Should delete and return 204")
    void deleteFruit_WhenExists_ShouldReturnNoContent() throws Exception {
        Fruit saved = fruitRepository.save(new Fruit(null, "Apple", 2));

        mockMvc.perform(delete("/fruits/" + saved.getId()))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/fruits/" + saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /fruits/{id} - When not exists should return 404")
    void deleteFruit_WhenNotExists_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/fruits/999"))
                .andExpect(status().isNotFound());
    }
}