package sk.upjs.ics.exceptions;

/**
 * Exception thrown when there is an issue accessing the ResultSet.
 */
public class CouldNotAccessResultSetException extends RuntimeException {
    /**
     * Constructs a new CouldNotAccessResultSetException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public CouldNotAccessResultSetException(String message, Throwable cause) {
        super(message, cause);
    }
}