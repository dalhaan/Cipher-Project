package javafxgui;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URISyntaxException;

public class Main extends Application {
	private PasswordField pfieldPassword, pfieldVerify;
	private TextArea textArea;
	private ProgressBar progressBar;
	private Label progressLabel;

	private boolean matching = false;
	
	public static void main(String[] args) throws URISyntaxException {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Setup window
		primaryStage.setTitle("AES Cipher");
		BorderPane layoutMain = new BorderPane();

		// Setup control panel
		GridPane layoutControl = new GridPane();
		layoutControl.setAlignment(Pos.CENTER);
		layoutControl.setGridLinesVisible(false);

		// Setup radio buttons
		ToggleGroup group = new ToggleGroup();
		VBox layoutRadioBtns = new VBox(10);
		RadioButton rbtnEncrypt = new RadioButton("Encrypt");
		rbtnEncrypt.setSelected(true);
		rbtnEncrypt.setToggleGroup(group);

		RadioButton rbtnDecrypt = new RadioButton("Decrypt");
		rbtnDecrypt.setToggleGroup(group);

		layoutRadioBtns.getChildren().addAll(rbtnEncrypt, rbtnDecrypt);
		layoutControl.addRow(0, layoutRadioBtns);
		layoutRadioBtns.setAlignment(Pos.CENTER);


		// Setup password fields
		VBox layoutPasswords = new VBox(10);
		pfieldPassword = new PasswordField();
		pfieldPassword.textProperty().addListener((value, oldValue, newValue) -> validatePasswords());
		//pfieldPassword.setStyle("-fx-control-inner-background: #FF4500");
		pfieldVerify = new PasswordField();
		pfieldVerify.textProperty().addListener((value, oldValue, newValue) -> validatePasswords());
		pfieldPassword.setPromptText("Enter password...");
		pfieldVerify.setPromptText("Verify password...");
		layoutPasswords.getChildren().addAll(pfieldPassword, pfieldVerify);
		layoutControl.addRow(1, layoutPasswords);
		layoutPasswords.setAlignment(Pos.CENTER);

		// Setup button
		Button button = new Button("OK");
		button.setPrefSize(160, 40);
		layoutControl.addRow(2, button);
		button.setAlignment(Pos.CENTER);
		button.setOnAction(e -> {
			textArea.appendText("Clicked.\n");
			if (pfieldPassword.getText().isEmpty()) {
				textArea.appendText("Password cannot be empty.\n");
			} else if (matching) {
				if (rbtnEncrypt.isSelected()) {
					encrypt(pfieldPassword.getText());
				} else {
					decrypt(pfieldPassword.getText());
				}
			}
		});

		// Setup progress bar
		progressBar = new ProgressBar();
		progressBar.setPrefWidth(160);
		layoutControl.addRow(3, progressBar);

		progressLabel = new Label("0/0");
		progressLabel.setAlignment(Pos.CENTER);
		layoutControl.addRow(4, progressLabel);

		// Setup control panel
		RowConstraints rcRadioBtn = new RowConstraints(70);
		RowConstraints rcPField = new RowConstraints(70);
		RowConstraints rcButton = new RowConstraints(70);
		RowConstraints rcProgressBar = new RowConstraints(40);
		RowConstraints rcProgressLabel = new RowConstraints(40);
		layoutControl.getRowConstraints().addAll(rcRadioBtn, rcPField, rcButton, rcProgressBar, rcProgressLabel);

		// Setup console panel
		ScrollPane scrollPane = new ScrollPane();

		textArea = new TextArea();
		textArea.prefWidthProperty().bind(primaryStage.widthProperty());
		textArea.setEditable(false);

		BorderPane layoutConsole = new BorderPane();
		scrollPane.setContent(textArea);
		layoutConsole.setCenter(scrollPane);

		layoutMain.setTop(layoutControl);
		layoutMain.setBottom(layoutConsole);

		Scene scene = new Scene(layoutMain, 200, 400);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * Validates the passwords entered in the password fields.
	 * If the passwords don't match the fields turn red, if they do they turn green.
	 */
	private void validatePasswords() {
		// Initialise method
		String password, verify;
		matching = false;
		password = pfieldPassword.getText();
		verify = pfieldVerify.getText();
		// Set appropriate colours
		if (verify.isEmpty()) {
			// If entering the first password, set both boxes white
			pfieldPassword.setStyle("-fx-control-inner-background: #FFFFFF");
			pfieldVerify.setStyle("-fx-control-inner-background: #FFFFFF");
		} else {
			if (!verify.equals(password)) {
				// If the verified password doesn't match the original, set both boxes red
				pfieldPassword.setStyle("-fx-control-inner-background: #FF5252");
				pfieldVerify.setStyle("-fx-control-inner-background: #FF5252");
			} else {
				// If they do match, set both boxes green
				pfieldPassword.setStyle("-fx-control-inner-background: #52ff52");
				pfieldVerify.setStyle("-fx-control-inner-background: #52ff52");
				matching = true;
			}
		}
	}

	private void encrypt(String key) {
		Task task = new EncryptTask(key);
		progressBar.progressProperty().bind(task.progressProperty());
		progressLabel.textProperty().bind(task.titleProperty());
		textArea.textProperty().bind(task.messageProperty());
		new Thread(task).start();
	}

	private void decrypt(String key) {
		Task task = new DecryptTask(key);
		progressBar.progressProperty().bind(task.progressProperty());
		progressLabel.textProperty().bind(task.titleProperty());
		textArea.textProperty().bind(task.messageProperty());
		new Thread(task).start();
	}
}
