package javafxgui;

import Encryption.Encryptor;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;

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
public class EncryptTask extends Task<Void> {
    private String key;

    private StringBuilder consoleBuilder = new StringBuilder();;

    public EncryptTask(String key) {
        this.key = key;
    }
    @Override
    protected Void call() throws Exception {
        if (key == null) {
            return null;
        }
        updateMessage("Starting to encrypt");
        encryptAll(key);
        return null;
    }

    public void encryptAll(String key) throws IOException {
        updateMessage("Start encryptAll("+key+")");
        updateProgress(0, 100);
        updateMessage("UpdateProgress(0, 100)");
        String path = Encryptor.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        updateMessage("Get class path: "+path);
        appendMessage(String.format("Encrypting entire directory: %s\n", path));

        File dir = new File(System.getProperty("user.dir"));
        File[] all = dir.listFiles();
        List<File> oldFiles = new ArrayList<File>();
        int i=1;
        for (File file : all) {
            if (file.isFile()) {
                if (!file.getName().equals(new File(path).getName())) {
                    appendMessage(String.format("Encrypting %s...", file.getName()));
                    try {
                        File output = new File(file.getName()+".enc");
                        Encryptor.encrypt(key, file, output);
                        oldFiles.add(file);
                        appendMessage(String.format("done\n"));
                    } catch (Exception e) {
                        appendMessage(String.format("failed: %s\n", e.getMessage()));
                    }
                }
            }
            updateTitle(i + "/" + all.length);
            updateProgress(((double)(i)/(double)all.length)*100, 100);
            i++;
        }
        for (File file : oldFiles) {
            file.delete();
        }
        appendMessage(String.format("Completed.\n\n"));
    }



    private void appendMessage(String str) {
        consoleBuilder.append(str);
        updateMessage(consoleBuilder.toString());
    }

}
