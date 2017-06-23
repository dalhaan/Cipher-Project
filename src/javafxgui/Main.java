package javafxgui;

import Encryption.CipherModel;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javax.crypto.Cipher;
import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

public class Main extends Application {
	private GuiController controller;
	private Stage window;
	private PasswordField pfieldPassword, pfieldVerify;
	private TextArea textArea;
	private ProgressBar progressBar;
	private Label progressLabel;
	private RadioButton rbtnEncrypt;
	private RadioButton rbtnDecrypt;
	private File[] selectedFiles = new File[0];
	private Label labelCount;

	private boolean matching = false;
	
	public static void main(String[] args) throws URISyntaxException {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.controller = new GuiController(new CipherModel(), this);
		this.window = primaryStage;
		// Setup window
		primaryStage.setTitle("AES Cipher");
		BorderPane layoutMain = new BorderPane();

		// Setup control panel
		GridPane layoutControl = new GridPane();
		layoutControl.setAlignment(Pos.CENTER);
		layoutControl.setGridLinesVisible(false);

		// Setup file selection button & label
		//// File selection button
		Button btnFileSelect = new Button("Select File(s)");
		btnFileSelect.setPrefSize(160, 40);
		btnFileSelect.setOnAction(e -> {
			controller.selectFiles();
		});
		layoutControl.setHalignment(btnFileSelect, HPos.CENTER);
		layoutControl.addRow(0, btnFileSelect);

		//// Selection count label
		labelCount = new Label("0 files selected");
		layoutControl.setHalignment(labelCount, HPos.CENTER);
		layoutControl.addRow(1, labelCount);

		// Setup radio buttons
		ToggleGroup group = new ToggleGroup();
		VBox layoutRadioBtns = new VBox(10);
		layoutRadioBtns.setAlignment(Pos.CENTER);
		rbtnEncrypt = new RadioButton("Encrypt");
		rbtnEncrypt.setSelected(true);
		rbtnEncrypt.setToggleGroup(group);

		rbtnDecrypt = new RadioButton("Decrypt");
		rbtnDecrypt.setToggleGroup(group);

		layoutRadioBtns.getChildren().addAll(rbtnEncrypt, rbtnDecrypt);
		layoutControl.setHalignment(btnFileSelect, HPos.CENTER);
		layoutControl.addRow(2, layoutRadioBtns);

		// Setup password fields
		VBox layoutPasswords = new VBox(10);
		layoutPasswords.setAlignment(Pos.CENTER);
		pfieldPassword = new PasswordField();
		pfieldPassword.textProperty().addListener((value, oldValue, newValue) -> controller.validatePasswords());
		pfieldVerify = new PasswordField();
		pfieldVerify.textProperty().addListener((value, oldValue, newValue) -> controller.validatePasswords());
		pfieldPassword.setPromptText("Enter password...");
		pfieldVerify.setPromptText("Verify password...");
		layoutPasswords.getChildren().addAll(pfieldPassword, pfieldVerify);
		layoutControl.setHalignment(labelCount, HPos.CENTER);
		layoutControl.addRow(3, layoutPasswords);

		// Setup button
		Button button = new Button("OK");
		button.setPrefSize(160, 40);
		layoutControl.setHalignment(button, HPos.CENTER);
		layoutControl.addRow(4, button);
		button.setOnAction(e -> {
			controller.doCipher();
		});

		// Setup progress bar
		progressBar = new ProgressBar();
		progressBar.setPrefWidth(160);
		progressBar.progressProperty().addListener((observable, oldValue, newValue) -> {
			// Set progress bar blue when processing and green when completed
			if (newValue.doubleValue() == 1.0) {
				progressBar.setStyle("-fx-accent: lime");
			} else {
				progressBar.setStyle("-fx-accent: royalblue");
			}
		});
		layoutControl.setHalignment(progressBar, HPos.CENTER);
		layoutControl.addRow(5, progressBar);
		// Setup progress label
		progressLabel = new Label("0/0");
		layoutControl.setHalignment(progressLabel, HPos.CENTER);
		layoutControl.addRow(6, progressLabel);

		// Setup control panel
		RowConstraints rcSelBtn = new RowConstraints(60);
		RowConstraints rcSelLbl = new RowConstraints(10);
		RowConstraints rcRadioBtn = new RowConstraints(70);
		RowConstraints rcPField = new RowConstraints(70);
		RowConstraints rcButton = new RowConstraints(60);
		RowConstraints rcProgressBar = new RowConstraints(30);
		RowConstraints rcProgressLabel = new RowConstraints(20);
		layoutControl.getRowConstraints().addAll(rcSelBtn, rcSelLbl, rcRadioBtn, rcPField, rcButton, rcProgressBar, rcProgressLabel);

		// Setup console panel
		textArea = new TextArea();
		textArea.setEditable(false);
		textArea.setStyle("-fx-font-size: 0.8em;");

		BorderPane layoutConsole = new BorderPane();
		layoutConsole.setCenter(textArea);
		layoutMain.setTop(layoutControl);
		layoutMain.setCenter(layoutConsole);

		// Setup window
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
		Task task = new EncryptTask(key, this.selectedFiles);
		progressBar.progressProperty().bind(task.progressProperty());
		progressLabel.textProperty().bind(task.titleProperty());
		textArea.textProperty().bind(task.messageProperty());
		new Thread(task).start();
		// Clear selected files once schedule is done
		selectFiles(null);
	}

	private void decrypt(String key) {
		Task task = new DecryptTask(key, this.selectedFiles);
		progressBar.progressProperty().bind(task.progressProperty());
		progressLabel.textProperty().bind(task.titleProperty());
		textArea.textProperty().bind(task.messageProperty());
		new Thread(task).start();
		// Clear selected files once schedule is done
		selectFiles(null);
	}

	/**
	 * Adds files as the selected files and updates the file count
	 * @param files
	 */
	private void selectFiles(List<File> files) {
		String value;
		if (files == null) {
			this.selectedFiles = new File[0];
			value = "0 selected files";
		} else {
			if (files.size() == 1) {
				value = "1 selected file";
			} else {
				value = files.size() + " selected files";
			}
			this.selectedFiles = files.toArray(new File[0]);
		}
		this.labelCount.setText(value);
	}

	public Stage getWindow() {
		return window;
	}

	public Label getSelectedFileLabel() {
		return labelCount;
	}

	public TextArea getConsole() {
		return textArea;
	}

	public PasswordField getPfieldPassword() {
		return pfieldPassword;
	}

	public PasswordField getPfieldVerify() {
		return pfieldVerify;
	}

	public int getCipherMode() {
		if (rbtnEncrypt.isSelected()) {
			return Cipher.ENCRYPT_MODE;
		} else if (rbtnDecrypt.isSelected()) {
			return Cipher.DECRYPT_MODE;
		}
		return -1;
	}

	public ProgressBar getProgressBar() {
		return this.progressBar;
	}

	public Label getProgressLabel() {
		return progressLabel;
	}
}
