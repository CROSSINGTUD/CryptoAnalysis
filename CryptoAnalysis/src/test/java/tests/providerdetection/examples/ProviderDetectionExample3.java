package tests.providerdetection.examples;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ProviderDetectionExample3 {

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException {
		Provider p1 = new BouncyCastleProvider();
		MessageDigest md = MessageDigest.getInstance("AES", p1);
		byte[] input = "message".getBytes();
		md.digest(input);
	}

}
