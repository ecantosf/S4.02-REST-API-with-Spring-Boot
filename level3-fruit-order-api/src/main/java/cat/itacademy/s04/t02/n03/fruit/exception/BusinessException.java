package cat.itacademy.s04.t02.n03.fruit.exception;

public class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }
}