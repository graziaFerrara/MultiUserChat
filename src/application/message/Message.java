package application.message;

import java.io.Serializable;

public class Message implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String sender, message, receiver = null;
	
	public Message(String sender, String message) {
		this.sender = sender;
		this.message = message;
	}
	
	public Message(String sender, String receiver, String message) {
		this(sender, message);
		this.receiver = receiver;
	}
	
	public String getSender() {
		return sender;
	}
	
	public String getReceiver() throws ReceiverNotSetException {
		if (receiver == null)
			throw new ReceiverNotSetException("Not set receiver!");
		else return receiver;
	}
	
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "Message [sender=" + sender + ", message=" + message + ", receiver=" + receiver + "]";
	}

}
