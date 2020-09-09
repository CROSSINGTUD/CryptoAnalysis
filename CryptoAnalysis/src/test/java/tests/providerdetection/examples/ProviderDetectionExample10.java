package tests.providerdetection.examples;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.util.Random;

import javax.crypto.KeyGenerator;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

public class ProviderDetectionExample10 {

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException {
		Random rand = new Random();
		int n = rand.nextInt(2);
		
		Provider p1 = new BouncyCastleProvider();
		
		if(n%2==0) {
			p1 = new BouncyCastleProvider();
		}
		else {
			p1 = new BouncyCastlePQCProvider();
		}
		
		KeyGenerator keygenerator = KeyGenerator.getInstance("AES", p1);
		keygenerator.generateKey();
	}

}
