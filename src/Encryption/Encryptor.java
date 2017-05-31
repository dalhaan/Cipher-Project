package Encryption;

import javafx.concurrent.Task;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryptor {
	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES/CBC/PKCS5PADDING";
	private static final boolean DEBUG = true;

	public static String encrypt(String key, String value) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
			byte[] toBytes = Base64.getDecoder().decode(value);
			byte[] encrypted = encrypt(key, toBytes);
			String encoded = Base64.getEncoder().encodeToString(encrypted);

			return encoded;
	}

	public static String decrypt(String key, String value) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
			byte[] toBytes = Base64.getDecoder().decode(value);
			byte[] decrypted = decrypt(key, toBytes);
			String encoded = Base64.getEncoder().encodeToString(decrypted);

			return encoded;
	}

	public static byte[] encrypt(String key, byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException,
			InvalidKeySpecException {
		
		byte[] keyStretch = PasswordClass.hash(key, 128);
		SecretKeySpec skeySpec = new SecretKeySpec(keyStretch, ALGORITHM);

		SecureRandom rand = new SecureRandom();
		IvParameterSpec iv = new IvParameterSpec(rand.generateSeed(16)); // Generate
																			// 16
																			// byte
																			// IV
		/*debug("Key: "+key);
		debug("Key hash: "+bytesToHex(keyStretch));
		debug("plaintext: "+bytesToHex(data));
		debug("IV: "+bytesToHex(iv.getIV()));*/
		
		// Init Cipher with key and iv
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

		// Encrypt bytes (doFinal)
		byte[] outputBytes = cipher.doFinal(data);
		//debug("Encrypted: "+bytesToHex(outputBytes));
		outputBytes = prefixHashAndIv(keyStretch, iv, outputBytes);
		//debug("Ouput: "+bytesToHex(outputBytes)+"\n\n");
		
		return outputBytes;
	}

	public static byte[] decrypt(String key, byte[] data)
			throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		byte[] keyStretch = PasswordClass.hash(key, 128);
		SecretKeySpec skeySpec = new SecretKeySpec(keyStretch, ALGORITHM);

		/*debug("Key: "+key);
		debug("Key hash: "+bytesToHex(keyStretch));*/
		// Check if hashed key matches the hashed key used
		byte[] hash = Arrays.copyOfRange(data, 0, 16);
		//debug("Derived key: "+bytesToHex(hash));
		if (!Arrays.equals(keyStretch, hash)) {
			throw new InvalidKeyException("Invalid Password");
		}

		// Extract IV & encrypted data from inputFile
		IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(data, 16, 32));
		byte[] encryptedBytes = Arrays.copyOfRange(data, 32, data.length);
		/*debug("Derived IV: "+bytesToHex(iv.getIV()));
		debug("Encrypted: "+bytesToHex(encryptedBytes));*/
		
		// Init Cipher with key and iv
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

		// Decrypt extracted encrypted data (doFinal)
		byte[] outputBytes = cipher.doFinal(encryptedBytes);
		//debug("plaintext: "+bytesToHex(outputBytes));
		return outputBytes;
	}

	public static void encrypt(String key, File inputFile, File outputFile) throws IOException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
		
		byte[] data = new byte[(int) inputFile.length()];
		
		FileInputStream inputStream = new FileInputStream(inputFile);
		inputStream.read(data);
		inputStream.close();
		
		byte[] encryptedData = encrypt(key, data);
		
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		outputStream.write(encryptedData);
		outputStream.close();
	}

	public static void decrypt(String key, File inputFile, File outputFile)
			throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		
		byte[] data = new byte[(int) inputFile.length()];
		
		FileInputStream inputStream = new FileInputStream(inputFile);
		inputStream.read(data);
		inputStream.close();

		byte[] decryptedData = decrypt(	key, data);
		
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		outputStream.write(decryptedData);
		outputStream.close();
	}

	/*
	 * public static void encrypt(String key, File inputFile, File outputFile) {
	 * // ENCRYPT
	 * 
	 * // Init Cipher with key and rand IV // Read inputFile into bytes //
	 * Encrypt bytes (doFinal) // Prefix IV to encrypted bytes // Write prefixed
	 * to outputFile // close streams try { byte[] keyStretch =
	 * Encryption.PasswordClass.hash(key, 128); SecretKeySpec skeySpec = new
	 * SecretKeySpec(keyStretch, ALGORITHM);
	 * 
	 * // Read inputFile into bytes FileInputStream inputStream; inputStream =
	 * new FileInputStream(inputFile);
	 * 
	 * byte[] inputBytes = new byte[(int) inputFile.length()];
	 * inputStream.read(inputBytes);
	 * 
	 * SecureRandom rand = new SecureRandom(); IvParameterSpec iv = new
	 * IvParameterSpec(rand.generateSeed(16)); // Generate // 16 // byte IV
	 * 
	 * // Init Cipher with key and iv Cipher cipher =
	 * Cipher.getInstance(TRANSFORMATION); cipher.init(Cipher.ENCRYPT_MODE,
	 * skeySpec, iv);
	 * 
	 * // Encrypt bytes (doFinal) byte[] outputBytes =
	 * cipher.doFinal(inputBytes);
	 * 
	 * FileOutputStream outputStream = new FileOutputStream(outputFile);
	 * outputStream.write(prefixHashAndIv(keyStretch, iv, outputBytes));
	 * 
	 * inputStream.close(); outputStream.close(); } catch (IOException e) {
	 * debug("File read/write error: " + e.getMessage()); } catch
	 * (IllegalBlockSizeException | BadPaddingException | InvalidKeyException |
	 * InvalidAlgorithmParameterException | NoSuchAlgorithmException |
	 * NoSuchPaddingException e) { debug("Cipher error: " + e.getMessage()); }
	 * catch (Exception e) { debug("Hashing error: " + e.getMessage()); } }
	 * 
	 * public static void decrypt(String key, File inputFile, File outputFile) {
	 * // DECRYPT
	 * 
	 * // Extract IV & encrypted data from inputFile // init Cipher with key and
	 * iv // Decrypt extracted encrypted data (doFinal)
	 * 
	 * // Read inputFile into bytes try { byte[] keyStretch =
	 * Encryption.PasswordClass.hash(key, 128); SecretKeySpec skeySpec = new
	 * SecretKeySpec(keyStretch, ALGORITHM);
	 * 
	 * FileInputStream inputStream = new FileInputStream(inputFile); byte[]
	 * inputBytes = new byte[(int) inputFile.length()];
	 * inputStream.read(inputBytes);
	 * 
	 * // Check if hashed key matches the hashed key used byte[] hash =
	 * Arrays.copyOfRange(inputBytes, 0, 16); if (!hash.equals(keyStretch)) {
	 * debug("Incorrect Password."); inputStream.close(); return; }
	 * 
	 * // Extract IV & encrypted data from inputFile IvParameterSpec iv = new
	 * IvParameterSpec(Arrays.copyOfRange(inputBytes, 16, 32)); byte[]
	 * encryptedBytes = Arrays.copyOfRange(inputBytes, 32, inputBytes.length);
	 * 
	 * // Init Cipher with key and iv Cipher cipher =
	 * Cipher.getInstance(TRANSFORMATION); cipher.init(Cipher.DECRYPT_MODE,
	 * skeySpec, iv);
	 * 
	 * // Decrypt extracted encrypted data (doFinal) byte[] outputBytes =
	 * cipher.doFinal(encryptedBytes);
	 * 
	 * FileOutputStream outputStream = new FileOutputStream(outputFile);
	 * outputStream.write(outputBytes);
	 * 
	 * inputStream.close(); outputStream.close();
	 * 
	 * } catch (IOException e) { debug("File read error: " + e.getMessage()); }
	 * catch (Exception e) { debug("Hashing error: " + e.getMessage()); } }
	 */

	public static void encryptAll(String key, TextArea textArea) throws IOException {
		String path = Encryptor.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		textArea.appendText(String.format("Encrypting entire directory: %s\n", path));

		File dir = new File(System.getProperty("user.dir"));
		File[] all = dir.listFiles();
		for (File file : all) {
			if (file.isFile()) {
				if (!file.getName().equals(new File(path).getName())) {
					textArea.appendText(String.format("Encrypting %s...", file.getName()));
					try {
						encrypt(key, file, file);
						textArea.appendText(String.format("done\n"));
					} catch (IOException e) {
						debug("File read/write error: " + e.getMessage());
						textArea.appendText(String.format("failed: %s\n", e.getMessage()));
					} catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException
							| InvalidAlgorithmParameterException | NoSuchAlgorithmException
							| NoSuchPaddingException e) {
						debug("Cipher error: " + e.getMessage());
						textArea.appendText(String.format("failed: %s\n", e.getMessage()));
					} catch (InvalidKeySpecException e) {
						debug("Hashing error: " + e.getMessage());
						textArea.appendText(String.format("failed: %s\n", e.getMessage()));
					}
				}
			}
		}
		textArea.appendText(String.format("Completed.\n\n"));
	}

	public static void decryptAll(String key, TextArea textArea) throws IOException {
		String path = Encryptor.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		textArea.appendText(String.format("Decrypting entire directory: %s\n", path));
		
		File dir = new File(System.getProperty("user.dir"));
		File[] all = dir.listFiles();
		for (File file : all) {
			if (file.isFile()) {
				if (!file.getName().equals(new File(path).getName())) {
					textArea.appendText(String.format("Decrypting %s...", file.getName()));
					try {
						decrypt(key, file, file);
						textArea.appendText(String.format("done\n"));
					} catch (IOException e) {
						debug("File read/write error: " + e.getMessage());
						textArea.appendText(String.format("failed: %s\n", e.getMessage()));
					} catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException
							| InvalidAlgorithmParameterException | NoSuchAlgorithmException
							| NoSuchPaddingException e) {
						debug("Cipher error: " + e.getMessage());
						textArea.appendText(String.format("failed: %s\n", e.getMessage()));
					} catch (InvalidKeySpecException e) {
						debug("Hashing error: " + e.getMessage());
						textArea.appendText(String.format("failed: %s\n", e.getMessage()));
					}
				}
			}
		}
		textArea.appendText(String.format("Completed.\n\n"));
	}

	public static String bytesToHex(byte[] in) {
		final StringBuilder builder = new StringBuilder();
		for (byte b : in) {
			builder.append(String.format("%02x ", b));
		}
		return builder.toString();
	}

	private static byte[] prefixHashAndIv(byte[] hash, IvParameterSpec iv, byte[] encryptedBytes) {
		int length;

		int hashLength = hash.length;
		int ivLength = iv.getIV().length;
		int initLength = encryptedBytes.length;
		length = hashLength + ivLength + initLength;

		byte[] ivBytes = iv.getIV();
		byte[] output = new byte[length];

		for (int i = 0; i < length; i++) {
			if (i < hashLength) {
				output[i] = hash[i];
			} else if (i < (hashLength + ivLength)) {
				output[i] = ivBytes[i - hashLength];
			} else {
				output[i] = encryptedBytes[i - hashLength - ivLength];
			}
		}

		return output;
	}

	private static void debug(String str) {
		if (DEBUG) {
			System.out.println(str);
		}
	}

	public static void main(String[] args) {

	}
}
