package simple.domain.exception;

public class QuantityException extends RuntimeException {
    public QuantityException(String message, Throwable cause) {
        super(message, cause);
    }
}
