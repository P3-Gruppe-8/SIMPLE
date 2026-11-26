package simple.domain.exception;

public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
