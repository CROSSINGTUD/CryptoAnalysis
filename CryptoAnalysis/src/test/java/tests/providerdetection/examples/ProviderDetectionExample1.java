package tests.providerdetection.examples;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import javax.crypto.KeyGenerator;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ProviderDetectionExample1 {

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException {
		Provider p1 = new BouncyCastleProvider();
		KeyGenerator keygenerator = KeyGenerator.getInstance("AES", p1);
		keygenerator.generateKey();
	}

}
