import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryptor {
	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES/CBC/PKCS5PADDING";

	public static String encrypt(String key, String value) {
		try {
			SecureRandom rand = new SecureRandom();
			IvParameterSpec iv = new IvParameterSpec(rand.generateSeed(16)); // Generate
																				// 16
																				// byte
																				// IV
			byte[] keyStretch = PasswordClass.hash(key, 128);
			SecretKeySpec skeySpec = new SecretKeySpec(keyStretch, ALGORITHM);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encryptedBytes = cipher.doFinal(value.getBytes());
			AESCipher aes = new AESCipher(encryptedBytes, iv);
			
			return aes.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String decrypt(String key, String encrypted) {
		try {
			byte[] keyStretch = PasswordClass.hash(key, 128);
			SecretKeySpec skeySpec = new SecretKeySpec(keyStretch, ALGORITHM);
			AESCipher aes = new AESCipher(encrypted);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, aes.getIv());

			byte[] decrypted = cipher.doFinal(aes.getEncryptedData());

			return new String(decrypted);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static void encrypt(String key, File inputFile, File outputFile, GUI gui) {
		String msg1 = "Encrypting file: "+inputFile.getName()+"... ";
		if (gui == null) {
			System.out.print(msg1);
		} else {
			gui.consoleAppend(msg1);
		}
		
		doCipher(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
		
		msg1 = "done";
		if (gui == null) {
			System.out.print(msg1);
		} else {
			gui.consoleAppend(msg1);
		}
	}
	
	public static void decrypt(String key, File inputFile, File outputFile, GUI gui) {
		String msg1 = "Decrypting file: "+inputFile.getName()+"... ";
		if (gui == null) {
			System.out.print(msg1);
		} else {
			gui.consoleAppend(msg1);
		}
		
		doCipher(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
		
		msg1 = "done";
		if (gui == null) {
			System.out.print(msg1);
		} else {
			gui.consoleAppend(msg1);
		}
	}

	public static void doCipher(int opmode, String key, File inputFile, File outputFile) {
		// ENCRYPT

		// Init Cipher with key and rand IV
		// Read inputFile into bytes
		// Encrypt bytes (doFinal)
		// Prefix IV to encrypted bytes
		// Write prefixed to outputFile
		// close streams

		// DECRYPT

		// Extract IV & encrypted data from inputFile
		// init Cipher with key and iv
		// Decrypt extracted encrypted data (doFinal)
		try {
			IvParameterSpec iv = null;
			byte[] keyStretch = PasswordClass.hash(key, 128);
			SecretKeySpec skeySpec = new SecretKeySpec(keyStretch, ALGORITHM);

			// Read inputFile into bytes
			FileInputStream inputStream = new FileInputStream(inputFile);
			byte[] inputBytes = new byte[(int) inputFile.length()];
			inputStream.read(inputBytes);

			byte[] dataBytes = null;

			if (opmode == Cipher.ENCRYPT_MODE) {
				SecureRandom rand = new SecureRandom();
				iv = new IvParameterSpec(rand.generateSeed(16)); // Generate 16
																	// byte IV
				dataBytes = inputBytes;
			} else if (opmode == Cipher.DECRYPT_MODE) {
				// Extract IV & encrypted data from inputFile
				AESCipher aesCipher = new AESCipher(inputBytes);
				iv = aesCipher.getIv();
				dataBytes = aesCipher.getEncryptedData();
			}

			// Init Cipher with key and iv
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(opmode, skeySpec, iv);

			// Encrypt bytes (doFinal)
			// Decrypt extracted encrypted data (doFinal)
			byte[] outputBytes = cipher.doFinal(dataBytes);

			FileOutputStream outputStream = new FileOutputStream(outputFile);

			if (opmode == Cipher.ENCRYPT_MODE) {
				// Prefix IV to encrypted bytes
				AESCipher aesCipher = new AESCipher(outputBytes, iv);
				// Write prefixed to outputBytes
				outputStream.write(aesCipher.getBytes());
			} else if (opmode == Cipher.DECRYPT_MODE) {
				// Write outputBYtes to outputFile
				outputStream.write(outputBytes);
			}

			// close streams
			inputStream.close();
			outputStream.close();

		} catch (IOException e) {
			System.out.println("IO error ("+e.getMessage()+")");
		} catch (IllegalBlockSizeException|BadPaddingException e) {
			System.out.println("Cipher error ("+e.getMessage()+")");
		} catch (InvalidKeyException|InvalidAlgorithmParameterException e) {
			System.out.println("Invalid password ("+e.getMessage()+")");
		} catch (NoSuchAlgorithmException|NoSuchPaddingException e) {
			System.out.println("Invalid algorithm ("+e.getMessage()+")");
		} catch (Exception e) {
			System.out.println("Hashing error ("+e.getMessage()+")");
		}
	}

	public static void main(String[] args) {
		String mode = args[0];
		String key = args[1];
		int cipherMode = 1;
		switch (mode) {
		case "-encrypt":	cipherMode = Cipher.ENCRYPT_MODE;
							break;
		case "-decrypt":	cipherMode = Cipher.DECRYPT_MODE;
							break;
		case "-encryptall": encryptAll(key, null);
							return;
		case "-decryptall": decryptAll(key, null);
		return;
		case "default":		return;
		}
		String inputFilename = args[2];
		String outputFilename = args[3];
		String operation = "single";
		try {
			operation = args[4];
		} catch (Exception e) {
			
		}
		
		File inputFile = new File(inputFilename);
		File outputFile = new File(outputFilename);
		
		System.out.printf("Key: %s\nInput File: %s\nOutput File: %s\n", key, inputFile.getName(), outputFile.getName());
		doCipher(cipherMode, key, inputFile, outputFile);
		System.out.println("Done.");
	}
	
	public static void encryptAll(String key, GUI gui) {
		String msg1 = "Encrypting files in cur dir:";
		if (gui == null) {
			System.out.println(msg1);
		} else {
			gui.consoleAppend(msg1);
		}
		
		File dir = new File(System.getProperty("user.dir"));
		File[] all = dir.listFiles();
		for (File file : all) {
			if (file.isFile()) {
				if (file.getName().equals(new File(Encryptor.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName()))
					continue;
				encrypt(key, file, file, gui);
			}
		}
		String msg2 = "Completed.";
		if (gui == null) {
			System.out.println(msg2);
		} else {
			gui.consoleAppend(msg2);
		}
	}
	
	public static void decryptAll(String key, GUI gui) {
		System.out.println("Decrypting files in cur dir:");
		File dir = new File(System.getProperty("user.dir"));
		File[] all = dir.listFiles();
		for (File file : all) {
			if (file.isFile()) {
				if (file.getName().equals(new File(Encryptor.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName()))
					continue;
				decrypt(key, file, file, gui);
			}
		}
		System.out.println("Completed.");
	}
	
	private static void encryptFiles(File[] files, String key) {
		// Encrypt files and filenames
		
		for (File file : files) {
			String fname = file.getName();
			doCipher(Cipher.ENCRYPT_MODE, key, file, file);
		}
	}
	
	public static String bytesToHex(byte[] in) {
	    final StringBuilder builder = new StringBuilder();
	    for(byte b : in) {
	        builder.append(String.format("%02x ", b));
	    }
	    return builder.toString();
	}
}
