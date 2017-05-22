import java.util.Arrays;
import java.util.Base64;

import javax.crypto.spec.IvParameterSpec;

public class AESCipher extends Node {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2602551196507096482L;
	private String key;
	private IvParameterSpec iv;
	private byte[] data;
	private boolean isEncrypted;
	
	public AESCipher(byte[] encrypted, IvParameterSpec iv) {
		this.data = encrypted;
		this.iv = iv;
		this.isEncrypted = true;
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
		this.data = encryptedData;
		this.isEncrypted = true;
	}
	
	public AESCipher(byte[] data, boolean isEncrypted) {
		if (isEncrypted) {
			byte[] iv = Arrays.copyOfRange(data, 0, 16); // Get 16 byte IV
			data = Arrays.copyOfRange(data, 16, data.length);
		
			this.iv = new IvParameterSpec(iv);
		}
		
		this.data = data;
		this.isEncrypted = isEncrypted;
	}
	
	public IvParameterSpec getIv() {
		return iv;
	}
	
	public byte[] getEncryptedData() {
		return data;
	}
	
	public String toString() {
		String encoded = Base64.getEncoder().encodeToString(isEncrypted?prefixIv(iv, data):data);
		return encoded;
	}
	
	public byte[] getBytes() {
		if (isEncrypted) {
			return prefixIv(iv, data);
		} else {
			return data;
		}
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
