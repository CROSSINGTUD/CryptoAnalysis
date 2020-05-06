package tests.providerdetection.examples;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.crypto.KeyGenerator;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ProviderDetectionExample2 {

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyGenerator keygenerator = KeyGenerator.getInstance("AES", new BouncyCastleProvider());
		keygenerator.generateKey();
	}

}
