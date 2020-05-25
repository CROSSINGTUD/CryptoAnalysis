package tests.providerdetection.examples;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class ProviderDetectionExample7 {

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException {
		String p1 = "BC";
		MessageDigest md = MessageDigest.getInstance("AES", p1);
		byte[] input = "message".getBytes();
		md.digest(input);
	}

}
