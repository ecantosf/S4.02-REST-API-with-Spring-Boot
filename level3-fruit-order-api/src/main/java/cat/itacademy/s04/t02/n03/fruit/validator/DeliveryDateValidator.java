package cat.itacademy.s04.t02.n03.fruit.validator;

import cat.itacademy.s04.t02.n03.fruit.dto.request.OrderRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DeliveryDateValidator 
        implements ConstraintValidator<FutureDeliveryDate, OrderRequestDTO> {
    
    @Override
    public boolean isValid(OrderRequestDTO request, ConstraintValidatorContext context) {
        if (request == null || request.deliveryDate() == null) {
            return true; // Let @NotNull handle null case
        }
        
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        boolean isValid = !request.deliveryDate().isBefore(tomorrow);
        
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Delivery date must be at least tomorrow (minimum: " + tomorrow + ")"
            ).addPropertyNode("deliveryDate").addConstraintViolation();
        }
        
        return isValid;
    }
}