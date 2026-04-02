package cat.itacademy.s04.t02.n03.fruit.config;

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
                .title("Fruit Order API - Level 3")
                .version("1.0")
                .description("REST API for managing fruit orders with MongoDB and embedded documents")
                .contact(new Contact()
                    .name("Your Name")
                    .email("your.email@example.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("http://springdoc.org")))
            .servers(List.of(
                new Server().url("http://localhost:8083").description("Development Server")
            ));
    }
}