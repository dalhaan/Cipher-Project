package javafxgui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {
	Stage window;
	
	public static void main(String[] args) {
		launch(args);
		// New change
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		window.setTitle("AES Cipher");
		
		Button button = new Button("OK");
		
		BorderPane layout = new BorderPane();
		layout.setLeft(button);
		
		Scene scene = new Scene(layout, 300, 350);
		window.setScene(scene);
		window.show();
	}

}
