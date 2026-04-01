package cat.itacademy.s04.t02.n03.fruit.model;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    private String id;

    @Field("client_name")
    @NotBlank(message = "Client name is required")
    @Size(min = 2, max = 100, message = "Client name must be between 2 and 100 characters")
    private String clientName;

    @Field("delivery_date")
    @NotNull(message = "Delivery date is required")
    @Indexed
    private LocalDate deliveryDate;

    @Field("items")
    @NotEmpty(message = "Order must have at least one item")
    private List<OrderItem> items;

    @Field("created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Field("updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Field("status")
    @Builder.Default
    private String status = "PENDING";
}