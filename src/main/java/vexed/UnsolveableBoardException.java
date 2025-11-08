package vexed;

public class UnsolveableBoardException extends RuntimeException {
    public UnsolveableBoardException(String message) {
        super(message);
    }

    public UnsolveableBoardException() {
        super();
    }
}
