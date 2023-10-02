package application.chatServer;

import java.io.IOException;
import application.message.Message;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;

public class ChatServerDocumentController {

	@FXML
	private ListView<String> usersList;
	@FXML
	private TableView<Message> messagesTable;
	@FXML
	private TableColumn<Message, String> senderClm;
	@FXML
	private TableColumn<Message, String> receiverClm;
	@FXML
	private TableColumn<Message, String> messageClm;

	private ChatServer server;
	private Alert error = null;

	/**
	 * Init the GUI.
	 */
	@FXML
	public void initialize() {
		
		try {
			
			// server creation
			server = new ChatServer(33333);

		} catch (IOException e) {
			
			// error in the server creation 
			error = new Alert(AlertType.ERROR, "Server creation failure!", ButtonType.OK);
			error.show();
			Platform.exit();
			return;
			
		}
		
		// table view setting
		usersList.setItems(server.getActiveUsers());
		
		senderClm.setCellValueFactory(new PropertyValueFactory<Message, String>("sender"));
		receiverClm.setCellValueFactory(new PropertyValueFactory<Message, String>("receiver"));
		messageClm.setCellValueFactory(new PropertyValueFactory<Message, String>("message"));

		messagesTable.setItems(server.getConsole());

		// start a task in parallel to the GUI having to manage users' connections requests
		Task<Void> task = new Task<>() {
			@Override
			public Void call() {
				try {
					while (true)
						server.acceptConnection();
				} catch (ClassNotFoundException | IOException e) {
					// failure in accepting the connection, do nothing
				}
				return null;
			}
		};

		new Thread(task).start();

	}

}
