import java.util.Arrays;
import java.util.Base64;

import javax.crypto.spec.IvParameterSpec;

public class AESCipher extends Node {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2602551196507096482L;
	private IvParameterSpec iv;
	private byte[] encrypted;
	
	public AESCipher(byte[] encrypted, IvParameterSpec iv) {
		this.encrypted = encrypted;
		this.iv = iv;
	}
	
	/**
	 * Decodes Base64 encryption into separate entities (16-byte IV + encrypted bytes)
	 * @param encrypted
	 * 			(16-byte IV + encrypted bytes)
	 */
	public AESCipher(String encrypted) {
		byte[] prefixed = Base64.getDecoder().decode(encrypted);
		byte[] iv = Arrays.copyOfRange(prefixed, 0, 16); // Get 16 byte IV
		byte[] encryptedData = Arrays.copyOfRange(prefixed, 16, prefixed.length);
		
		this.iv = new IvParameterSpec(iv);
		this.encrypted = encryptedData;
	}
	
	public AESCipher(byte[] prefixed) {
		byte[] iv = Arrays.copyOfRange(prefixed, 0, 16); // Get 16 byte IV
		byte[] encryptedData = Arrays.copyOfRange(prefixed, 16, prefixed.length);
		
		this.iv = new IvParameterSpec(iv);
		this.encrypted = encryptedData;
	}
	
	public IvParameterSpec getIv() {
		return iv;
	}
	
	public byte[] getEncryptedData() {
		return encrypted;
	}
	
	public String toString() {
		byte[] prefixed = prefixIv(iv, encrypted);
		String encoded = Base64.getEncoder().encodeToString(prefixed);
		return encoded;
	}
	
	public byte[] getBytes() {
		return prefixIv(iv, encrypted);
	}
	
	private byte[] prefixIv(IvParameterSpec iv, byte[] encryptedBytes) {
		int length;
		
		int ivLength = iv.getIV().length;
		int initLength = encryptedBytes.length;
		length = ivLength + initLength;
		
		byte[] ivBytes = iv.getIV();
		byte[] output = new byte[length];
		
		for (int i=0; i<length; i++) {
			if (i < ivLength) {
				output[i] = ivBytes[i];
			} else {
				output[i] = encryptedBytes[i-ivLength];
			}
		}
		
		return output;
	}

	@Override
	public void decrypt() {
		
	}
}
