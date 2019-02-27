package crypto.providerdetection;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.util.Random;

import javax.crypto.KeyGenerator;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

public class ProviderDetectionExample {
	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException {
		
		Random rand = new Random();

		// Obtain a number between [0 - 49].
		int n = rand.nextInt(50);
		System.out.println(n);
		
		Provider p1 = new BouncyCastlePQCProvider();
		Provider p2 = new BouncyCastleProvider();
		String pString = "BC";
		KeyGenerator keygenerator = KeyGenerator.getInstance("AES", pString);
		//((n%2==0) ? p1 : p2)
	}
}
