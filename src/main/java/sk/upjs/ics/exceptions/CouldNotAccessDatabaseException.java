package sk.upjs.ics.exceptions;

public class CouldNotAccessDatabaseException extends RuntimeException {
    public CouldNotAccessDatabaseException(String message) {
        super(message);
    }
}
