package javafxgui;

import Encryption.CipherModel;
import javafx.concurrent.Task;
import javafx.scene.control.PasswordField;
import javafx.stage.FileChooser;

import javax.crypto.Cipher;
import java.io.File;
import java.util.List;

/**
 * Created by Sirius on 16/06/2017.
 */
public class GuiController {
    private Main view;
    private CipherModel model;

    public GuiController(CipherModel model, Main view) {
        this.model = model;
        this.view = view;
    }

    public void selectFiles() {
        String path = System.getProperty("user.dir");
        File directory = new File(path);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(directory);

        selectFiles(fileChooser.showOpenMultipleDialog(view.getWindow()));
    }

    /**
     * Adds files as the selected files and updates the file count
     * @param files
     */
    private void selectFiles(List<File> files) {
        String value;
        if (files == null) {
            model.updateSelectedFiles(new File[0]);
            value = "0 selected files";
        } else {
            if (files.size() == 1) {
                value = "1 selected file";
            } else {
                value = files.size() + " selected files";
            }
            model.updateSelectedFiles(files.toArray(new File[0]));
        }
        view.getSelectedFileLabel().setText(value);
    }

    /**
     * Validates the passwords entered in the password fields.
     * If the passwords don't match the fields turn red, if they do they turn green.
     */
    public void validatePasswords() {
        // Initialise method
        String password, verify;
        PasswordField pField = view.getPfieldPassword();
        PasswordField pFieldVerify = view.getPfieldVerify();
        model.isMatching(false);
        password = view.getPfieldPassword().getText();
        verify = view.getPfieldVerify().getText();
        // Set appropriate colours
        if (pFieldVerify.getText().isEmpty()) {
            // If entering the first password, set both boxes white
            pField.setStyle("-fx-control-inner-background: #FFFFFF");
            pFieldVerify.setStyle("-fx-control-inner-background: #FFFFFF");
        } else {
            if (!pFieldVerify.getText().equals(password)) {
                // If the verified password doesn't match the original, set both boxes red
                pField.setStyle("-fx-control-inner-background: #FF5252");
                pFieldVerify.setStyle("-fx-control-inner-background: #FF5252");
            } else {
                // If they do match, set both boxes green
                pField.setStyle("-fx-control-inner-background: #52ff52");
                pFieldVerify.setStyle("-fx-control-inner-background: #52ff52");
                model.isMatching(true);
            }
        }
    }

    public void doCipher() {
        view.getConsole().appendText("Clicked.\n");
        if (view.getPfieldPassword().getText().isEmpty()) {
            view.getConsole().appendText("Password cannot be empty.\n");
        } else if (model.isMatching()) {
            model.setKey(view.getPfieldPassword().getText());
            if (view.getCipherMode() == Cipher.ENCRYPT_MODE) {
                encrypt();
            } else {
                decrypt();
            }
        }
    }

    private void encrypt() {
        // Start encryption
        Task task = model.encrypt();
        // Bind view to model
        view.getProgressBar().progressProperty().bind(task.progressProperty());
        view.getProgressLabel().textProperty().bind(task.titleProperty());
        view.getConsole().textProperty().bind(task.messageProperty());
    }

    private void decrypt() {
        // Start encryption
        Task task = model.decrypt();
        // Bind view to model
        view.getProgressBar().progressProperty().bind(task.progressProperty());
        view.getProgressLabel().textProperty().bind(task.titleProperty());
        view.getConsole().textProperty().bind(task.messageProperty());
    }
}
