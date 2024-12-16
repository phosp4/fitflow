package sk.upjs.ics.exceptions;

/**
 * Exception thrown when there is an issue connecting to the database.
 */
public class CouldNotConnectToDatabaseException extends RuntimeException {
    /**
     * Constructs a new CouldNotConnectToDatabaseException with the specified detail message.
     *
     * @param message the detail message
     */
    public CouldNotConnectToDatabaseException(String message) {
        super(message);
    }
}