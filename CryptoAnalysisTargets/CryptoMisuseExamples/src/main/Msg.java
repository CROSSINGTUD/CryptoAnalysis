package main;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Signature;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class Msg {
	private String ALG = "AES";

	public byte[] sign(String data) throws GeneralSecurityException{
		Signature signature = Signature.getInstance("SHA");
		signature.initSign(getPrivateKey());
		signature.update(data.getBytes());
		return signature.sign();
	}
//
	public PrivateKey getPrivateKey() throws GeneralSecurityException {
		KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA");
		kpGen.initialize(118);
		KeyPair keyPair = kpGen.generateKeyPair();
		return keyPair.getPrivate();
	}
	
//	public static void main(String...args) throws GeneralSecurityException {
//		Msg msg = new Msg();
////		msg.sign("test");
//	}
	
	public void encrypt() throws GeneralSecurityException, BadPaddingException {
		Cipher c = Cipher.getInstance("AES");
	}

	public void encryptAlgFromVar() throws GeneralSecurityException, BadPaddingException {
		String alg = "AES";
		Cipher c = Cipher.getInstance(alg);
	}

	public void encryptAlgFromField() throws GeneralSecurityException, BadPaddingException {
		ALG = "Test";
		Cipher c = Cipher.getInstance(ALG);
	}
}
