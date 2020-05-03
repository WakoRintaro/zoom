package socket;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class Encrypt {
	// アルゴリズム、ブロック、パディング
	private final String ALGORITHM = "AES/CBC/PKCS5Padding";
	// 共通鍵
	private final String ENCRYPT_KEY = "HWcDBfYkJcH3wut3";
	private final SecretKeySpec key = new SecretKeySpec(ENCRYPT_KEY.getBytes(), "AES");
	
	private byte[] iv = new byte[16];
	private IvParameterSpec iv_sks;
	
	private SecureRandom srandom;
	private Cipher cipher;

	public Encrypt() {
		srandom = new SecureRandom();
		// TODO Auto-generated constructor stub
		try {
			cipher = Cipher.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void regenerateIv() {
		srandom.nextBytes(iv);
		iv_sks = new IvParameterSpec(iv);
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key, iv_sks);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void resetIv(byte[] iv) {
		iv_sks = new IvParameterSpec(iv);
		try {
			cipher.init(Cipher.DECRYPT_MODE, key, iv_sks);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public byte[] encrypt(byte[] data) {
		// +16 for padding, +16 for initial vector
		int encrypted_length = (int)(Math.floor(data.length/16)+1) * 16 + 16;
		byte[] encrypted = new byte[encrypted_length];
		byte[] encrypted_data = null;

		// Regenerate initial vector for each packet
		regenerateIv();
		
		byte[] iv = cipher.getIV();
		
		try {
			encrypted_data = cipher.doFinal(data);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// add initial vector at the end of encrypted data
		 System.arraycopy(encrypted_data, 0, encrypted, 0, encrypted_data.length);
		 System.arraycopy(iv, 0, encrypted, encrypted_data.length, iv.length);
	
		return encrypted;
	}
	public byte[] decrypt(byte[] encrypted) {
		// Separate initial vector from encrypted data
		byte[] iv = Arrays.copyOfRange(encrypted, encrypted.length-16, encrypted.length);
		byte[] encrypted_data = Arrays.copyOfRange(encrypted, 0, encrypted.length-16);
		resetIv(iv);
		
		byte[] decrypted_data = null;
		
		try {
			decrypted_data = cipher.doFinal(encrypted_data);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return decrypted_data;
		
	}
}
