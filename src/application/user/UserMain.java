package application.user;
	
import javafx.application.Application;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class UserMain extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(UserMain.class.getResource("UserDocument.fxml"));
	        primaryStage.setTitle("Chat");
	        primaryStage.setScene(new Scene(root));
	        primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
