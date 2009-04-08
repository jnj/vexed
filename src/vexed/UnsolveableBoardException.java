package vexed;

public class UnsolveableBoardException extends RuntimeException {

	private final static long serialVersionUID = 1;
	
	public UnsolveableBoardException() {
		super();
	}

	public UnsolveableBoardException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsolveableBoardException(String message) {
		super(message);
	}

	public UnsolveableBoardException(Throwable cause) {
		super(cause);
	}
}
