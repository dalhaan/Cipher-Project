package Encryption;

import javafx.concurrent.Task;
import javafxgui.DecryptTask;
import javafxgui.EncryptTask;

import java.io.File;
import java.util.List;

/**
 * Created by Sirius on 16/06/2017.
 */
public class CipherModel {
    private File[] selectedFiles = new File[0];
    private String key;

    /**
     * Adds files as the selected files and updates the file count
     * @param selectedFiles
     */
    public void updateSelectedFiles(File[] selectedFiles) {
        this.selectedFiles = selectedFiles;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public Task encrypt() {
        Task task = new EncryptTask(this.key, this.selectedFiles);
        Thread encryptThread = new Thread(task);
        encryptThread.start();
        // Clear selected files once schedule is done
        updateSelectedFiles(null);
        return task;
    }

    public Task decrypt() {
        Task task = new DecryptTask(this.key, this.selectedFiles);
        Thread decryptThread = new Thread(task);
        decryptThread.start();
        // Clear selected files once schedule is done
        updateSelectedFiles(null);
        return task;
    }
}