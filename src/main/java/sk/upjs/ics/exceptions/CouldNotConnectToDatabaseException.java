package sk.upjs.ics.exceptions;

public class CouldNotConnectToDatabaseException extends  RuntimeException {
    public CouldNotConnectToDatabaseException(String message) {
        super(message);
    }
}
