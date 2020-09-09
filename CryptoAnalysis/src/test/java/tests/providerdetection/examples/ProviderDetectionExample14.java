package tests.providerdetection.examples;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Random;

import javax.crypto.KeyGenerator;

public class ProviderDetectionExample14 {

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException {
		Random rand = new Random();
		int n = rand.nextInt(2);
		
		String pString1 = "BC";
		
		switch(n) {
		case 0:
			pString1="BC";
			break;
		case 1: 
			pString1="BCPQC";
			break;
		case 2:
			pString1="BC";
			break;
		default:
			pString1="BCPQC";
			break;
		}
		
		KeyGenerator keygenerator = KeyGenerator.getInstance("AES", pString1);
		keygenerator.generateKey();
	}

}
