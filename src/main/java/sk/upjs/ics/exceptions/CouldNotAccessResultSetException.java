package sk.upjs.ics.exceptions;

public class CouldNotAccessResultSetException extends RuntimeException {
    public CouldNotAccessResultSetException(String message, Throwable cause) {
        super(message, cause);
    }
}
