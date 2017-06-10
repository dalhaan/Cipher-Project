package javafxgui;

import Encryption.Encryptor;
import javafx.concurrent.Task;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sirius on 30/05/2017.
 */
public class EncryptTask extends Task<Void> {
    private String key;
    private File[] files;

    private StringBuilder consoleBuilder = new StringBuilder();;

    public EncryptTask(String key) {
        this.key = key;
    }

    public EncryptTask(String key, File[] files) {
        this(key);
        this.files = files;
    }

    @Override
    protected Void call() throws Exception {
        if (key == null) {
            return null;
        }
        updateMessage("Starting to encrypt");
        if (files != null) {
            encryptFiles(this.key, this.files);
        } else {
            encryptAll(key);
        }
        return null;
    }

    /** Encrypt every file in the current directory.
     * Calling this method encrypts every files in the current directory and gives them the extension .enc
     * @param key
     * @throws IOException
     */
    public void encryptAll(String key) throws IOException {
        // Load all of the files in the current directory
        File dir = new File(System.getProperty("user.dir"));
        File[] all = dir.listFiles();
        // Encrypt the files
        encryptFiles(key, all);
        // Alert the user of completion
        appendMessage(String.format("Completed.\n\n"));
    }

    /**
     * Encrypt a group of files.
     * Calling this method encrypts the given files into new files with a .enc extension and deletes the original files once they have all been encrypted.
     * @param key
     * @param files
     */
    private void encryptFiles(String key, File[] files) {
        // Initialise method
        String path = Encryptor.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        List<File> oldFiles = new ArrayList<File>();
        updateProgress(0, 100);
        // Encrypt each given file
        int i=1;
        for (File file : files) {
            // Check that the file is a file, not a subdirectory. Don't encrypt subdirectories.
            if (file.isFile()) {
                // Don't encrypt this application's file
                if (!file.getName().equals(new File(path).getName())) {
                    appendMessage(String.format("Encrypting %s...", file.getName()));
                    try {
                        // Encrypt the file
                        File output = new File(file.getCanonicalPath()+".enc");
                        Encryptor.encrypt(key, file, output);
                        // Keep track of original files to delete later
                        oldFiles.add(file);
                        appendMessage(String.format("done\n"));
                    } catch (Exception e) {
                        // Encryption of the file failed
                        appendMessage(String.format("failed: %s\n", e.getMessage()));
                        e.printStackTrace();
                    }
                }
            }
            // Update progress indicators
            updateTitle(i + "/" + files.length);
            updateProgress(i, files.length);
            i++;
        }
        // Delete the original files so the user doesn't have to manually
        for (File file : oldFiles) {
            file.delete();
        }
    }

    /**
     * Append a string to the console output
     * @param str
     */
    private void appendMessage(String str) {
        consoleBuilder.append(str);
        updateMessage(consoleBuilder.toString());
    }
}
