package main;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Signature;

public class Msg {
	public byte[] sign(String data) throws GeneralSecurityException{
		Signature signature = Signature.getInstance("SHA");
		signature.initSign(getPrivateKey());
		signature.update(data.getBytes());
		return signature.sign();
	}

	public PrivateKey getPrivateKey() throws GeneralSecurityException {
		KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA");
		kpGen.initialize(118);
		KeyPair keyPair = kpGen.generateKeyPair();
		return keyPair.getPrivate();
	}
	
	public static void main(String...args) throws GeneralSecurityException {
		Msg msg = new Msg();
		msg.sign("test");
	}
}
