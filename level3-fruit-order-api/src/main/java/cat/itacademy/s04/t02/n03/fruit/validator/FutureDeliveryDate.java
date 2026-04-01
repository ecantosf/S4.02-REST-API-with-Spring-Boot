package cat.itacademy.s04.t02.n03.fruit.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DeliveryDateValidator.class)
@Documented
public @interface FutureDeliveryDate {
    
    String message() default "Delivery date must be at least tomorrow";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}