package org.glassfish.grizzly.config.ssl;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class CustomClass {
	public void init(SecretKey k, String algs) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException{
		Cipher instance = Cipher.getInstance(algs);
		instance.init(2, k);
		byte[] ci = instance.doFinal("test".getBytes());
	}
	public void call() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		String param = "AES";
		init(null, param);
	}
}
