package tests.providerdetection.examples;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ProviderDetectionExample4 {

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException {
		MessageDigest md = MessageDigest.getInstance("AES", new BouncyCastleProvider());
		byte[] input = "message".getBytes();
		md.digest(input);
	}

}
