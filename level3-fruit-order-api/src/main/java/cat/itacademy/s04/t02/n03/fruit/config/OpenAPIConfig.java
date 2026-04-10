package cat.itacademy.s04.t02.n03.fruit.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fruit Order API - Level 3 (MongoDB)")
                        .version("1.0")
                        .description("REST API for managing fruit orders with MongoDB and embedded documents. Part of the IT Academy Spring Boot project.")
                        .contact(new Contact()
                                .name("Eduard Cantos Font")
                                .email("ecantosf@example.com"))
                        .license(new License()
                                .name("Educational License")
                                .url("https://www.itacademy.cat")))
                .servers(List.of(
                        new Server().url("http://localhost:8083").description("Development Server")
                ))
                .externalDocs(new ExternalDocumentation()
                        .description("Project Repository")
                        .url("https://github.com/ecantosf/S4.02-REST-API-with-Spring-Boot"));
    }
}