package cat.itacademy.s04.t02.n02.fruit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cat.itacademy.s04.t02.n02.fruit.dto.request.ProviderRequestDTO;
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

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ProviderController Integration Tests")
class ProviderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private FruitRepository fruitRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        fruitRepository.deleteAll();
        providerRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /providers - Should create provider and return 201")
    void createProvider_ShouldReturnCreated() throws Exception {
        ProviderRequestDTO request = new ProviderRequestDTO("Fresh Fruits", "Spain");

        mockMvc.perform(post("/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Fresh Fruits"))
                .andExpect(jsonPath("$.country").value("Spain"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("POST /providers - Duplicate name should return 409")
    void createProvider_WithDuplicateName_ShouldReturnConflict() throws Exception {
        providerRepository.save(new Provider(null, "Fresh Fruits", "Spain", new ArrayList<>()));

        ProviderRequestDTO request = new ProviderRequestDTO("Fresh Fruits", "France");

        mockMvc.perform(post("/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("already exists")));
    }

    @Test
    @DisplayName("GET /providers - Should return all providers")
    void getAllProviders_ShouldReturnList() throws Exception {
        providerRepository.save(new Provider(null, "Fresh Fruits", "Spain", new ArrayList<>()));
        providerRepository.save(new Provider(null, "Tropical Fruits", "Brazil", new ArrayList<>()));

        mockMvc.perform(get("/providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Fresh Fruits"))
                .andExpect(jsonPath("$[1].name").value("Tropical Fruits"));
    }

    @Test
    @DisplayName("GET /providers/{id} - When exists should return 200")
    void getProviderById_WhenExists_ShouldReturnOk() throws Exception {
        Provider saved = providerRepository.save(new Provider(null, "Fresh Fruits", "Spain", new ArrayList<>()));

        mockMvc.perform(get("/providers/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fresh Fruits"))
                .andExpect(jsonPath("$.country").value("Spain"));
    }

    @Test
    @DisplayName("GET /providers/{id} - When not exists should return 404")
    void getProviderById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/providers/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("GET /providers/{id}/details - Should return provider with fruits")
    void getProviderWithFruits_ShouldReturnDetails() throws Exception {
        Provider provider = providerRepository.save(new Provider(null, "Fresh Fruits", "Spain", new ArrayList<>()));
        fruitRepository.save(new Fruit(null, "Apple", 2, provider));
        fruitRepository.save(new Fruit(null, "Banana", 3, provider));

        mockMvc.perform(get("/providers/" + provider.getId() + "/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fresh Fruits"))
                .andExpect(jsonPath("$.fruits.length()").value(2))
                .andExpect(jsonPath("$.fruits[0].name").value("Apple"))
                .andExpect(jsonPath("$.fruits[1].name").value("Banana"));
    }

    @Test
    @DisplayName("PUT /providers/{id} - Should update and return 200")
    void updateProvider_WhenExists_ShouldReturnOk() throws Exception {
        Provider saved = providerRepository.save(new Provider(null, "Fresh Fruits", "Spain", new ArrayList<>()));
        ProviderRequestDTO updateRequest = new ProviderRequestDTO("Fresh Fruits SL", "Spain");

        mockMvc.perform(put("/providers/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fresh Fruits SL"))
                .andExpect(jsonPath("$.country").value("Spain"));
    }

    @Test
    @DisplayName("DELETE /providers/{id} - With fruits should return 400")
    void deleteProvider_WithFruits_ShouldReturnBadRequest() throws Exception {
        Provider provider = providerRepository.save(new Provider(null, "Fresh Fruits", "Spain", new ArrayList<>()));
        fruitRepository.save(new Fruit(null, "Apple", 2, provider));

        mockMvc.perform(delete("/providers/" + provider.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Cannot delete provider")));
    }

    @Test
    @DisplayName("DELETE /providers/{id} - Without fruits should return 204")
    void deleteProvider_WithoutFruits_ShouldReturnNoContent() throws Exception {
        Provider provider = providerRepository.save(new Provider(null, "Fresh Fruits", "Spain", new ArrayList<>()));

        mockMvc.perform(delete("/providers/" + provider.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/providers/" + provider.getId()))
                .andExpect(status().isNotFound());
    }
}
