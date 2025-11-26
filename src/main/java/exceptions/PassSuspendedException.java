package exceptions;

public class PassSuspendedException extends RuntimeException {
    public PassSuspendedException(Long passId) {
        super("Pass " + passId + " is suspended and cannot be used.");
    }
}

