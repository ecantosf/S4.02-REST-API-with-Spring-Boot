package cat.itacademy.s04.t02.n03.fruit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "cat.itacademy.s04.t02.n03.fruit.repository")
public class MongoConfig {
    // Configuration class for MongoDB auditing
    // @EnableMongoAuditing enables automatic filling of @CreatedDate and @LastModifiedDate
}
