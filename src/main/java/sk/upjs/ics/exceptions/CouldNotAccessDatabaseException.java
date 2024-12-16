package sk.upjs.ics.exceptions;

/**
 * Exception thrown when there is an issue accessing the database.
 */
public class CouldNotAccessDatabaseException extends RuntimeException {
    /**
     * Constructs a new CouldNotAccessDatabaseException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public CouldNotAccessDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}