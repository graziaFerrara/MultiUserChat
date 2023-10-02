package application.user;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

import application.message.Message;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

public class UserDocumentController {
	@FXML
	private TextField portField;
	@FXML
	private TextField IPField;
	@FXML
	private TextField loginUsernameField;
	@FXML
	private Button connectButton;
	@FXML
	private MenuItem disconnectButton;
	@FXML
	private TableView<Message> table;
	@FXML
	private TableColumn<Message, String> usersClm;
	@FXML
	private TableColumn<Message, String> mexClm;
	@FXML
	private TextField usernameField;
	@FXML
	private TextField messageField;
	@FXML
	private Button sendButton;
	@FXML
	private BorderPane loginPane, chatPane;

	private User user = null;
	private Alert error = null;

	/**
	 * Init the GUI.
	 */
	@FXML
	public void initialize() {

		chatPane.setVisible(false);
		loginPane.setVisible(true);

		usersClm.setCellValueFactory(new PropertyValueFactory<Message, String>("sender"));
		mexClm.setCellValueFactory(new PropertyValueFactory<Message, String>("message"));

		error = new Alert(AlertType.ERROR, "Not valid fields, retry!", ButtonType.OK);

	}

	/**
	 * Called when the Submit button is pressed. It tries to login to the server. If
	 * the fields have been correctly specified, and there are no errors during the
	 * connection, the user is logged in, otherwise an alert is shown.
	 * 
	 * @param event
	 */
	@FXML
	public void connect(ActionEvent event) {

		String IPAddress = IPField.getText(), username = loginUsernameField.getText(), portString = portField.getText();

		if (checkLoginFields(IPAddress, portString, username)) {

			try {

				int port = Integer.parseInt(portField.getText());
				user = new User(IPAddress, port);
				user.setUsername(username);

				if (user.connect(disconnectButton)) {

					chatPane.setVisible(true);
					loginPane.setVisible(false);

					table.setItems(user.getIncomingMessages());

				} else {
					// not valid fields, retry message
					error.setContentText("Connection failed, retry!");
					error.show();
				}

				clearLoginFields();

			} catch (NumberFormatException e) {
				// not valid port alert message
				error.setContentText("Not valid port, retry!");
				error.show();
			}

		} else {
			// missing fields, retry message
			error.setContentText("Missing fields, retry!");
			error.show();
		}

	}

	/**
	 * Called when the Disconnect menu item is pressed. It tries to disconnect the
	 * user from the client. If the disconnection is not successful, it shows an
	 * alert message.
	 * 
	 * @param event
	 */
	@FXML
	public void disconnect(ActionEvent event) {
		try {
			user.disconnect();
			clearChatFields();
			table.refresh();
			chatPane.setVisible(false);
			loginPane.setVisible(true);
		} catch (IOException e) {
			// disconnection failure message
			error.setContentText("Disconnection error, retry!");
			error.show();
		}
	}

	/**
	 * Called when the Send button is pressed. It tries to send a message to a
	 * certain user, if the receiver is specified in the appropriate field, in
	 * broadcast otherwise. If something goes wrong in the meantime, an alert
	 * message is shown.
	 * 
	 * @param event
	 */
	@FXML
	public void send(ActionEvent event) {

		String message = messageField.getText(), receiver = usernameField.getText();

		if (!message.isEmpty())
			try {
				if (receiver.isEmpty())
					// broadcast
					user.sendMessage(message);
				else
					// one to one
					user.sendMessage(receiver, message);
			} catch (IOException e) {
				// error sending the message, retry
				error.setContentText("Error sending the message, retry!");
				error.show();
			}
		else {
			// error message not specified
			error.setContentText("Message not specified, retry!");
			error.show();
		}

	}

	/**
	 * Clears the login fields.
	 */
	private void clearLoginFields() {
		portField.clear();
		IPField.clear();
		loginUsernameField.clear();
	}

	/**
	 * Clears the chat fields.
	 */
	private void clearChatFields() {
		usernameField.clear();
		messageField.clear();
	}

	/**
	 * Checks that the login fields are not empty.
	 * 
	 * @param IP
	 * @param port
	 * @param username
	 * @return true if all the fields have been filled, false otherwise
	 */
	private boolean checkLoginFields(String IP, String port, String username) {
		if (!IP.isEmpty() && !port.isEmpty() && !username.isEmpty())
			return true;
		else
			return false;
	}

}
