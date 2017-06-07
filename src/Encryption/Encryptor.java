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
	private static int blockSize = 4096;
	private static int hashLength = 16;
	private static int ivLength = 16;

    /**
     * Encrypt a file.
     * This method encrypts an input file with a string and saves the file into the output file
     * @param key
     * @param inputFile
     * @param outputFile
     * @throws IOException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeySpecException
     */
	public static void encrypt(String key, File inputFile, File outputFile) throws IOException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
		FileInputStream inputStream = new FileInputStream(inputFile);
		FileOutputStream outputStream = new FileOutputStream(outputFile, true);

		// Create keySpec and Iv
		byte[] keyStretch = PasswordClass.hash(key, 128);
		byte[] hash = PasswordClass.hash(Base64.getEncoder().encodeToString(keyStretch), 128);
		SecretKeySpec skeySpec = new SecretKeySpec(hash, ALGORITHM);
		SecureRandom rand = new SecureRandom();
		IvParameterSpec iv = new IvParameterSpec(rand.generateSeed(16)); // Generate 16byte IV

		// Init Cipher with key and iv
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

		byte[] block = new byte[blockSize];
		int bytesRead;
		int i=0;
		while ((bytesRead = inputStream.read(block, 0, blockSize)) != -1) {
			// Encrypt the block
			byte[] output = cipher.update(block, 0, bytesRead);
			if (output != null) {
				if (i==0) {
					output = prefixHashAndIv(hash, iv, output);
				}
				// Write the now encrypted block into the first block of the output file
				outputStream.write(output);
			}
			i++;
		}
		outputStream.write(cipher.doFinal());
		inputStream.close();
		outputStream.close();
	}

    /**
     * Decrypts a file.
     * This method decrypts the input file with a string and saves it into the output file.
     * @param key
     * @param inputFile
     * @param outputFile
     * @throws IOException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
	public static void decrypt(String key, File inputFile, File outputFile)
			throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		FileInputStream inputStream = new FileInputStream(inputFile);
		// Verify password with the hashed password of the file
		byte[] keyStretch = PasswordClass.hash(key, 128);
		byte[] hash = PasswordClass.hash(Base64.getEncoder().encodeToString(keyStretch), 128);
		byte[] fileHash = new byte[hashLength];
		inputStream.read(fileHash, 0, hashLength);
		if (!Arrays.equals(hash, fileHash)) {
			// If passwords don't match throw exception and close the input file
			inputStream.close();
			throw new InvalidKeyException(("Wrong password"));
		}

		// Otherwise, open stream to the output file
		FileOutputStream outputStream = new FileOutputStream(outputFile);

		// Load the initialisation vector from the input file
		byte[] ivBytes = new byte[ivLength];
		inputStream.read(ivBytes, 0, ivLength);
		IvParameterSpec iv = new IvParameterSpec(ivBytes);
		SecretKeySpec skeySpec = new SecretKeySpec(hash, ALGORITHM);

		// Init Cipher with key and iv
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

		// Write decrypted file to the output file block by block
		byte[] block = new byte[blockSize];
		int bytesRead;
		while((bytesRead = inputStream.read(block, 0, blockSize)) != -1) {
			byte[] output = cipher.update(block, 0, bytesRead);
			// Write the now decrypted block into the first block of the output file
			if (output != null) outputStream.write(output);
		}
		outputStream.write(cipher.doFinal());
		// Finished decrypting file so close all streams
		outputStream.close();
		inputStream.close();
	}

    /**
     * Convert bytes to a hex string.
     * @param in
     * @return
     */
	public static String bytesToHex(byte[] in) {
		final StringBuilder builder = new StringBuilder();
		for (byte b : in) {
			builder.append(String.format("%02x ", b));
		}
		return builder.toString();
	}

    /**
     * Prefixes the input hash and IV to the encryptedBytes array.
     * @param hash
     * @param iv
     * @param encryptedBytes
     * @return
     */
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
}
