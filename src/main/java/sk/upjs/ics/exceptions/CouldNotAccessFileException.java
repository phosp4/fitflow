package sk.upjs.ics.exceptions;

/**
 * Exception thrown when there is an issue accessing a file.
 */
public class CouldNotAccessFileException extends RuntimeException {
    /**
     * Constructs a new CouldNotAccessFileException with the specified detail message.
     *
     * @param message the detail message
     */
    public CouldNotAccessFileException(String message) {
        super(message);
    }
}