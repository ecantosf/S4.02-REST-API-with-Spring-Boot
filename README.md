# Fruit API - Spring Boot REST API Project (Task S4.02)  
**Description**:  In this task, three independent Spring Boot applications will be 
developed, each exposing a REST API that implements a full CRUD (Create, Read, 
Update, Delete) for different entities. Three different databases will be used:

| Level | Database | Description |
|-------|----------|-------------|
| Level 1 | H2 | Basic CRUD for fruit inventory management |
| Level 2 | MySQL | CRUD with relationships (Fruit - Provider) |
| Level 3 | MongoDB | CRUD for fruit orders with embedded documents |

## Project Structure
.
├── level1-fruit-api-h2/ # Level 1: H2 in-memory database
├── level2-fruit-api-mysql/ # Level 2: MySQL with JPA relationships
└── level3-fruit-order-api/ # Level 3: MongoDB with embedded documents

## Exercise Statements

### Level 1 (CRUD exercise with H2):
Complete REST API for fruit inventory management with H2 database.
- **Entities**: Fruit (id, name, weightInKilos)
- **Endpoints**: POST, GET, PUT, DELETE for /fruits
- **Validation**: @NotBlank, @Positive, @Size, @Max
- **Error Handling**: GlobalExceptionHandler with custom exceptions
- **Testing**: Unit tests (Mockito) and integration tests (MockMvc)
- **Documentation**: Swagger/OpenAPI available at /swagger-ui.html

### Level 2 (CRUD exercise with MySQL):
Complete REST API for fruit and provider management with MySQL database and JPA relationships.
- **Entities**: Provider (id, name, country), Fruit (id, name, weightInKilos) with @OneToMany/@ManyToOne relationship
- **Endpoints**: Full CRUD for /providers and /fruits, plus filtering fruits by provider
- **Validation**: @NotBlank, @Positive, @Size, duplicate name validation
- **Error Handling**: GlobalExceptionHandler with ResourceNotFoundException, DuplicateResourceException, BusinessException
- **Testing**: Unit tests (Mockito) and integration tests (MockMvc) - 39 tests total
- **Documentation**: Swagger/OpenAPI available at /swagger-ui.html
- **Database**: MySQL with Docker Compose

### Level 3 (CRUD exercise with MongoDB):
*Implementation pending*

## Features

### Level 1 (Completed)
- ✅ Full CRUD operations for Fruit entity
- ✅ H2 in-memory database with JPA/Hibernate
- ✅ DTO pattern with Bean Validation
- ✅ Global exception handling (404, 400, 500)
- ✅ Unit tests with Mockito (6 tests)
- ✅ Integration tests with MockMvc (7 tests)
- ✅ Docker multi-stage build
- ✅ OpenAPI/Swagger documentation
- ✅ H2 Console available at /h2-console

### Level 2 (Completed)
- ✅ Full CRUD operations for Provider and Fruit entities
- ✅ MySQL database with JPA/Hibernate
- ✅ Bidirectional relationship @OneToMany / @ManyToOne
- ✅ DTO pattern with nested provider data in fruit responses
- ✅ Custom exceptions: DuplicateResourceException, BusinessException
- ✅ Global exception handling (404, 409, 400, 500)
- ✅ Unit tests with Mockito (19 tests: 9 ProviderService + 10 FruitService)
- ✅ Integration tests with MockMvc (20 tests: 9 ProviderController + 11 FruitController)
- ✅ Docker multi-stage build and docker-compose for MySQL
- ✅ OpenAPI/Swagger documentation
- ✅ phpMyAdmin available at http://localhost:8080

### Level 3
- ⏳ Planned

## Technologies
- Java 21
- Spring Boot 3.2.4
- Spring Data JPA
- Spring Web
- H2 Database (Level 1)
- MySQL (Level 2)
- MongoDB (Level 3 - planned)
- Lombok
- Maven 3.8+
- Docker Desktop (for Level 2 and Level 3)
- OpenAPI/Swagger (springdoc-openapi)

## Installation & Running

### Prerequisites
- Java 21 (LTS)
- Maven 3.8+
- Git
- Docker Desktop (optional, for Level 2 and Level 3)

### Clone the repository
```bash
git clone https://github.com/ecantosf/S4.02-REST-API-with-Spring-Boot.git
cd S4.02-REST-API-with-Spring-Boot
```

### Author
Eduard Cantos Font

### License
This project is for educational purposes as part of the IT Academy course.