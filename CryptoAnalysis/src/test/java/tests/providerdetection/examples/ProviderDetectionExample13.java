package tests.providerdetection.examples;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Random;

import javax.crypto.KeyGenerator;

public class ProviderDetectionExample13 {

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException {
		Random rand = new Random();
		int n = rand.nextInt(2);
		
		String pString1 = "BC";
		
		if(n%2==0) {
			pString1 = "BC";
		}
		else {
			pString1 = "BCPQC";
		}
		
		KeyGenerator keygenerator = KeyGenerator.getInstance("AES", pString1);
		keygenerator.generateKey();
	}

}
