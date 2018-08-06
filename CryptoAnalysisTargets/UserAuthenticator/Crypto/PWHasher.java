
package Crypto; 

import java.security.InvalidAlgorithmParameterException;

import java.security.InvalidKeyException;

import java.security.NoSuchAlgorithmException;

import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;

import javax.crypto.BadPaddingException;

import javax.crypto.Cipher;

import javax.crypto.IllegalBlockSizeException;

import javax.crypto.NoSuchPaddingException;

import java.security.SecureRandom;

import javax.crypto.spec.IvParameterSpec;

import javax.crypto.spec.SecretKeySpec;

import java.security.spec.InvalidKeySpecException;

import java.util.List;

import java.util.Base64;

import java.io.InputStream;

import java.io.OutputStream;

import java.util.Properties;

import java.io.FileOutputStream;

/** @author CogniCrypt */
public class PWHasher {	
	//adopted code from https://github.com/defuse/password-hashing
	
	public String createPWHash(char[] pwd) throws GeneralSecurityException { 
		byte[] salt = new byte[224/8];
		SecureRandom.getInstanceStrong().nextBytes(salt);
		
		PBEKeySpec spec = new PBEKeySpec(pwd, salt, 65536, 224);
		SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA224");
		String pwdHash = toBase64(salt) + ":" + toBase64(f.generateSecret(spec).getEncoded());
		spec.clearPassword();
		return pwdHash;
	}
	
	public Boolean verifyPWHash(char[] pwd, String pwdhash) throws GeneralSecurityException {
		String[] parts = pwdhash.split(":");
		byte[] salt = fromBase64(parts[0]);

		PBEKeySpec spec = new PBEKeySpec(pwd, salt, 65536, 224);
		SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA224");
		Boolean areEqual = slowEquals(f.generateSecret(spec).getEncoded(), fromBase64(parts[1]));
		spec.clearPassword();
		return areEqual;
	}
	
	private static boolean slowEquals(byte[] a, byte[] b) {
		int diff = a.length ^ b.length;
		for (int i = 0; i < a.length && i < b.length; i++) {
			diff |= a[i] ^ b[i];
		}	
		return diff == 0;
	}

	private static String toBase64(byte[] array) {
		return DatatypeConverter.printBase64Binary(array);
	}

	private static byte[] fromBase64(String hash) {
		return DatatypeConverter.parseBase64Binary(hash);
	}
}
