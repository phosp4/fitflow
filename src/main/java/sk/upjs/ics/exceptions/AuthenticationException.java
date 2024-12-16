package sk.upjs.ics.exceptions;

/**
 * Exception thrown when authentication fails.
 */
public class AuthenticationException extends Exception {
    /**
     * Constructs a new AuthenticationException with the specified detail message.
     *
     * @param message the detail message
     */
    public AuthenticationException(String message) {
        super(message);
    }
}