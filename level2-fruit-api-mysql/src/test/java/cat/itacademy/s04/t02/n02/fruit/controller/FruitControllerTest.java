package cat.itacademy.s04.t02.n02.fruit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cat.itacademy.s04.t02.n02.fruit.dto.request.FruitRequestDTO;
import cat.itacademy.s04.t02.n02.fruit.model.Fruit;
import cat.itacademy.s04.t02.n02.fruit.model.Provider;
import cat.itacademy.s04.t02.n02.fruit.repository.FruitRepository;
import cat.itacademy.s04.t02.n02.fruit.repository.ProviderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

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
    private ProviderRepository providerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Provider testProvider;

    @BeforeEach
    void setUp() {
        fruitRepository.deleteAll();
        providerRepository.deleteAll();
        testProvider = providerRepository.save(new Provider(null, "Fresh Fruits", "Spain", new ArrayList<>()));
    }

    @Test
    @DisplayName("POST /fruits - Should create fruit and return 201")
    void createFruit_ShouldReturnCreated() throws Exception {
        FruitRequestDTO request = new FruitRequestDTO("Apple", 2, testProvider.getId());

        mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Apple"))
                .andExpect(jsonPath("$.weightInKilos").value(2))
                .andExpect(jsonPath("$.provider.id").value(testProvider.getId()));
    }

    @Test
    @DisplayName("POST /fruits - Invalid provider should return 404")
    void createFruit_WithInvalidProvider_ShouldReturnNotFound() throws Exception {
        FruitRequestDTO request = new FruitRequestDTO("Apple", 2, 999L);

        mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /fruits - Should return all fruits with provider data")
    void getAllFruits_ShouldReturnAllWithProvider() throws Exception {
        fruitRepository.save(new Fruit(null, "Apple", 2, testProvider));

        mockMvc.perform(get("/fruits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Apple"))
                .andExpect(jsonPath("$[0].provider.id").value(testProvider.getId()));
    }

    @Test
    @DisplayName("GET /fruits/{id} - When exists should return 200")
    void getFruitById_WhenExists_ShouldReturnOk() throws Exception {
        Fruit saved = fruitRepository.save(new Fruit(null, "Apple", 2, testProvider));

        mockMvc.perform(get("/fruits/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Apple"))
                .andExpect(jsonPath("$.weightInKilos").value(2))
                .andExpect(jsonPath("$.provider.id").value(testProvider.getId()));
    }

    @Test
    @DisplayName("GET /fruits/{id} - When not exists should return 404")
    void getFruitById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/fruits/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /fruits/by-provider - Should return fruits for provider")
    void getFruitsByProvider_ShouldReturnList() throws Exception {
        fruitRepository.save(new Fruit(null, "Apple", 2, testProvider));
        fruitRepository.save(new Fruit(null, "Banana", 3, testProvider));

        mockMvc.perform(get("/fruits/by-provider")
                        .param("providerId", String.valueOf(testProvider.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Apple"))
                .andExpect(jsonPath("$[1].name").value("Banana"));
    }

    @Test
    @DisplayName("GET /fruits/by-provider - Invalid provider should return 404")
    void getFruitsByProvider_WithInvalidProvider_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/fruits/by-provider")
                        .param("providerId", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /fruits/{id} - Should update fruit and return 200")
    void updateFruit_WhenExists_ShouldReturnOk() throws Exception {
        Fruit saved = fruitRepository.save(new Fruit(null, "Apple", 2, testProvider));
        FruitRequestDTO updateRequest = new FruitRequestDTO("Green Apple", 3, testProvider.getId());

        mockMvc.perform(put("/fruits/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Green Apple"))
                .andExpect(jsonPath("$.weightInKilos").value(3));
    }

    @Test
    @DisplayName("PUT /fruits/{id} - When not exists should return 404")
    void updateFruit_WhenNotExists_ShouldReturnNotFound() throws Exception {
        FruitRequestDTO updateRequest = new FruitRequestDTO("Green Apple", 3, testProvider.getId());

        mockMvc.perform(put("/fruits/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /fruits/{id} - Should delete fruit and return 204")
    void deleteFruit_WhenExists_ShouldReturnNoContent() throws Exception {
        Fruit saved = fruitRepository.save(new Fruit(null, "Apple", 2, testProvider));

        mockMvc.perform(delete("/fruits/" + saved.getId()))
                .andExpect(status().isNoContent());

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
