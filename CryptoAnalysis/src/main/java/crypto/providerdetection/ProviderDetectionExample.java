package crypto.providerdetection;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.util.Random;

import javax.crypto.KeyGenerator;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

import com.google.inject.spi.Message;

public class ProviderDetectionExample {
	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException {
		
		Random rand = new Random();
		// Obtain a number between [0 - 49].
		int n = rand.nextInt(50);
		System.out.println(n);
		
//		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		Provider p1 = new BouncyCastleProvider();
		Provider p2 = new BouncyCastlePQCProvider();
		
		String pString1 = "BC";
		String pString2 = "BCPQC";
//		MessageDigest md = MessageDigest.getInstance("AES", BouncyCastleProvider.PROVIDER_NAME);
		
		KeyGenerator keygenerator = KeyGenerator.getInstance("AES", p2);
		
		//((n%2==0) ? p1 : p2)
		//((n%2==0) ? pString1 : pString2)
	}
}
