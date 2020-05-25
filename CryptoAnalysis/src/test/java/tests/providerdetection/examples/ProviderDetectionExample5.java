package tests.providerdetection.examples;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.crypto.KeyGenerator;

public class ProviderDetectionExample5 {

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException {
		String p1 = "BC";
		KeyGenerator keygenerator = KeyGenerator.getInstance("AES", p1);
		keygenerator.generateKey();
	}

}
