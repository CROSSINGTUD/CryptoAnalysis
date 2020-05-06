package tests.providerdetection.examples;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Random;

import javax.crypto.KeyGenerator;

public class ProviderDetectionExample12 {

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException {
		Random rand = new Random();
		int n = rand.nextInt(2);
		
		String pString1 = "BC";
		String pString2 = "BCPQC";
		
		KeyGenerator keygenerator = KeyGenerator.getInstance("AES", ((n%2==0) ? pString1 : pString2));
		keygenerator.generateKey();
	}

}
