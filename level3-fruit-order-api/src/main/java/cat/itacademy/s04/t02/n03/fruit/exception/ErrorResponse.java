package cat.itacademy.s04.t02.n03.fruit.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
    int status,
    String message,
    LocalDateTime timestamp,
    Map<String, String> errors
) {
    public ErrorResponse(int status, String message) {
        this(status, message, LocalDateTime.now(), null);
    }
    
    public ErrorResponse(int status, String message, Map<String, String> errors) {
        this(status, message, LocalDateTime.now(), errors);
    }
}