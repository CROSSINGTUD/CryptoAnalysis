package tests.providerdetection.examples;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class ProviderDetectionExample8 {

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException {
		MessageDigest md = MessageDigest.getInstance("AES", "BC");
		byte[] input = "message".getBytes();
		md.digest(input);
	}

}
