package application.user;

import java.io.*;
import java.net.*;
import application.message.Message;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;

public class User implements Runnable {

	private String IPAddress;
	private int portNumber;
	private Thread t = null;
	private static String username = null;
	private static Socket userSocket = null;
	private static ObjectOutputStream os = null;
	private static ObjectInputStream is = null;
	private static boolean disconnected = false;
	private static MenuItem disconnectButton = null;

	private static ObservableList<Message> incomingMessages = FXCollections.observableArrayList(); // list to be shown
																									// in the table view

	/**
	 * Creates a new user.
	 * 
	 * @param IPAddress  used to create the Socket
	 * @param portNumber used to create the Socket
	 */
	public User(String IPAddress, int portNumber) {
		this.IPAddress = IPAddress;
		this.portNumber = portNumber;
	}

	/**
	 * Sets the user's username.
	 * 
	 * @param username
	 */
	public void setUsername(String username) {
		User.username = username;
	}

	/**
	 * Returns the list of messages received by the client.
	 * 
	 * @return incomingMessages
	 */
	public ObservableList<Message> getIncomingMessages() {
		return incomingMessages;
	}

	/**
	 * Tries to connect the client to the server.
	 * @param disconnectButton 
	 * 
	 * @return true if the connection succedes, false otherwise.
	 */
	public boolean connect(MenuItem disconnectButton) {

		try {
			userSocket = new Socket(IPAddress, portNumber);
			os = new ObjectOutputStream(userSocket.getOutputStream());
			os.writeUTF(username);
			os.flush();
			User.disconnectButton = disconnectButton;
		} catch (IOException e) {
			return false;
		}

		User user = new User(IPAddress, portNumber);
		t = new Thread(user);
		t.start();

		return true;

	}

	/**
	 * Tries to disconnect the client from the server.
	 * 
	 * @throws IOException
	 */

	public void disconnect() throws IOException {
		disconnected = true;
		incomingMessages.clear();
		userSocket.close();
	}

	/**
	 * Send a message in broadcast to all the active users.
	 * 
	 * @param message
	 * @throws IOException
	 */
	public void sendMessage(String message) throws IOException {
		if (os != null) {
			os.writeObject(new Message(username, message));
			os.flush();
		} else
			throw new IOException("Open output stream first!");
	}

	/**
	 * Send a message to the specified user.
	 * 
	 * @param receiver
	 * @param message
	 * @throws IOException
	 */
	public void sendMessage(String receiver, String message) throws IOException {
		if (os != null) {
			os.writeObject(new Message(username, receiver, message));
			os.flush();
		}
	}

	/**
	 * Reads the incoming messages and adds them to the incomingMessages observable
	 * list.
	 * 
	 * @return message
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Message readIncomingMessages() throws IOException, ClassNotFoundException {

		Message message = (Message) is.readObject();

		// to ask the GUI to be updated as soon as it can, since it cannot be updated
		// by the thread executing this method
		Platform.runLater(() -> {
			incomingMessages.add(message);
		});

		return message;

	}

	/**
	 * The thread runs in parallel to the GUI and is used to continously read the
	 * incoming messages, if any. If there are no incoming messages, it does
	 * nothing.
	 */
	@Override
	public void run() {

		while (!disconnected)

			try {
				if (is == null)
					is = new ObjectInputStream(userSocket.getInputStream());
				readIncomingMessages();
			} catch (EOFException e){
				// server aborted, back to login
				disconnectButton.fire();
			} catch (ClassNotFoundException | IOException e) {
				// No message present to be read, do nothing
			}

	}

}
