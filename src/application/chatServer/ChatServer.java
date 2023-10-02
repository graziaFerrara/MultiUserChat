package application.chatServer;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;
import application.message.Message;
import application.message.ReceiverNotSetException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ChatServer implements Runnable {

	private static ObservableList<Message> console = FXCollections.observableArrayList(); // list to be shown in the
																							// tableview
	private static ObservableList<String> activeUsers = FXCollections.observableArrayList(); // list to be shown in the
																								// listview
	private static HashMap<String, Socket> users = new HashMap<>();
	private static HashMap<Socket, ObjectOutputStream> connections = new HashMap<>();
	private static ServerSocket serverSocket;
	private ObjectInputStream is = null;
	private String username = null;

	/**
	 * Server creation when launching the application.
	 * 
	 * @param port
	 * @throws IOException
	 */
	public ChatServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
	}

	/**
	 * Server created by the thread.
	 * 
	 * @param username
	 */
	public ChatServer(String username, ObjectInputStream is) {
		this.username = username;
		this.is = is;
	}

	/**
	 * It is executed by a thread running in parallel to the GUI which countinuously
	 * tries to accept user connections and to create a thread to manage its
	 * requests. If the username specified by the client is already in use, the
	 * input stream is closed and this also closes the socket, so the client is
	 * unable to connect. If the username is valid, the user is connected and is
	 * added to the active users structure, then a thread for the management of the
	 * user's request is created and started.
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws UsernameAlreadyInUseException
	 */
	public void acceptConnection() throws IOException, ClassNotFoundException {

		Socket newUser = serverSocket.accept();

		is = new ObjectInputStream(newUser.getInputStream());
		String username = is.readUTF();

		if (!users.containsKey(username)) {

			users.put(username, newUser);
			// this method is contiously executed by a task running in parallel to the GUI,
			// so the updates have to be done as soon as possible by the GUI
			Platform.runLater(() -> {
				activeUsers.add(username);
			});

			ChatServer cs = new ChatServer(username, is);
			Thread t = new Thread(cs);
			t.start();
		}

	}

	/**
	 * Disconnects the user. The socket associated to the specified user is closed
	 * and removed from the structure, then the user is removed from the activeUsers
	 * structure.
	 * 
	 * @param username
	 * @throws IOException
	 */
	public void disconnectUser(String username) throws IOException {

		users.get(username).close();
		users.remove(username);

		// this method is contiously executed by a task running in parallel to the GUI,
		// so the updates have to be done as soon as possible by the GUI
		Platform.runLater(() -> {
			activeUsers.remove(username);
		});

	}

	/**
	 * Sends in broadcast the given message. It iterates over the structure
	 * containing the users and the coresponding socket. If an output stream with
	 * that specific user had already been opened, it is retrieved from the
	 * structure containing it and used to send the message, otherwise it is
	 * created, added to the structure and then used to send the message.
	 * 
	 * @param message
	 * @throws IOException
	 */
	private void broadcast(Message message) throws IOException {

		ObjectOutputStream os = null;

		for (Entry<String, Socket> user : users.entrySet()) {
			String receiver = user.getKey();
			Socket receiverSocket = user.getValue();

			if (!receiver.equals(message.getSender()))
				if (!connections.containsKey(receiverSocket)) {
					os = new ObjectOutputStream(user.getValue().getOutputStream());
					os.writeObject(message);
					os.flush();
					connections.put(receiverSocket, os);
				} else {
					os = connections.get(receiverSocket);
					os.writeObject(message);
					os.flush();
				}
		}

	}

	/**
	 * Sends to the specified receiver the given message. If there already was an
	 * output stream for that receiver, it is retreived from the structure and used,
	 * otherwise it is created, then added to the structure and used.
	 * 
	 * @param message
	 * @throws ReceiverNotSetException
	 * @throws IOException
	 */
	private void oneToOne(Message message) throws IOException {

		ObjectOutputStream os = null;

		String receiver;
		try {
			receiver = message.getReceiver();
			if (activeUsers.contains(receiver)) {
				Socket receiverSocket = users.get(receiver);

				if (!connections.containsKey(receiverSocket)) {
					os = new ObjectOutputStream(receiverSocket.getOutputStream());
					os.writeObject(message);
					os.flush();
					connections.put(receiverSocket, os);
				} else {
					os = connections.get(receiverSocket);
					os.writeObject(message);
					os.flush();
				}
			} else {
				// receiver not active, do nothing
			}

		} catch (ReceiverNotSetException e) {
			// receiver not set, do nothing
		}

	}

	/**
	 * Tries to read incoming messages from the input stream.
	 * 
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Message readIncomingMessages() throws IOException, ClassNotFoundException {
		return (Message) is.readObject();
	}

	/**
	 * Each thread managing a given user's request executes the folling code. It
	 * tries to read the incoming messages then, if the receiver is specified and is
	 * valid, it forwards the message to it, otherwise if it is not specified, it
	 * sends the message in broadcast. If the client disconnects the EOFException is
	 * thrown and the client can be disconnected from the server.
	 */
	@Override
	public void run() {

		while (true) {

			try {

				Message message = readIncomingMessages();

				try {
					String receiver = message.getReceiver();
					if (users.containsKey(receiver)) {
						oneToOne(message);
						console.add(message);
						System.out.println(message.toString());
					} else {
						// not valid receiver, do nothing
					}

				} catch (ReceiverNotSetException e) {
					message.setReceiver("everyone");
					broadcast(message);
					console.add(message);
					System.out.println(message.toString());
				}	

			} catch (EOFException e) {

				try {
					disconnectUser(username);
					break;
				} catch (IOException e1) {
					// IO error, do nothing
				}

			} catch (ClassNotFoundException | IOException e) {
				// IO error, do nothing
			}

		}

	}

	/**
	 * Returns the messages structure.
	 * 
	 * @return
	 */
	public ObservableList<Message> getConsole() {
		return console;
	}

	/**
	 * Returns the active users list.
	 * 
	 * @return
	 */
	public ObservableList<String> getActiveUsers() {
		return activeUsers;
	}

}
