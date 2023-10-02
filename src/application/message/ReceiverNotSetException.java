package application.message;

public class ReceiverNotSetException extends Exception{

	private static final long serialVersionUID = 1L;

	public ReceiverNotSetException(String errorMessage) {
		super(errorMessage);
	}
}
