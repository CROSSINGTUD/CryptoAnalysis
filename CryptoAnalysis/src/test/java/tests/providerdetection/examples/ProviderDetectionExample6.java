package tests.providerdetection.examples;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.KeyGenerator;

public class ProviderDetectionExample6 {

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyGenerator keygenerator = KeyGenerator.getInstance("AES", "BC");
		keygenerator.generateKey();
	}

}
