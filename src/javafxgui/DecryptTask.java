package javafxgui;

import Encryption.Encryptor;
import javafx.concurrent.Task;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sirius on 30/05/2017.
 */
public class DecryptTask extends Task<Void> {
    private String key;
    private File[] files;
    private StringBuilder consoleBuilder = new StringBuilder();

    public DecryptTask(String key) {
        this.key = key;
    }

    public DecryptTask(String key, File[] files) {
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
            decryptFiles(this.key, this.files);
        } else {
            decryptAll(key);
        }
        return null;
    }

    public void decryptAll(String key) throws IOException {
        File dir = new File(System.getProperty("user.dir"));
        File[] all = dir.listFiles();
        decryptFiles(key, all);
        appendMessage(String.format("Completed.\n\n"));
    }

    public void decryptFiles(String key, File[] files) {
        String path = Encryptor.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        List<File> oldFiles = new ArrayList<File>();
        updateProgress(0, 100);
        int i=1;
        for (File file : files) {
            if (file.isFile()) {
                if (!file.getName().equals(new File(path).getName())) {
                    appendMessage(String.format("Decrypting %s...", file.getName()));
                    try {
                        File output = new File(file.getCanonicalPath().substring(0, file.getCanonicalPath().lastIndexOf('.')));
                        Encryptor.decrypt(key, file, output);
                        oldFiles.add(file);
                        appendMessage(String.format("done\n"));
                    } catch (Exception e) {
                        appendMessage(String.format("failed: %s\n", e.getMessage()));
                    }
                }
            }
            updateTitle(i + "/" + files.length);
            updateProgress(((double)i/(double)files.length)*100, 100);
            i++;
        }
        for (File file : oldFiles) {
            file.delete();
        }
    }
    private void appendMessage(String str) {
        consoleBuilder.append(str);
        updateMessage(consoleBuilder.toString());
    }

}
