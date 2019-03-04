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

public class ProviderDetectionExample {
	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException {
		
		Random rand = new Random();
		// Obtain a number between [0 - 49].
		int n = rand.nextInt(2);
		System.out.println(n);
		
//		Security.addProvider(new BouncyCastleProvider());
//		int a = Security.addProvider(new BouncyCastleProvider());
//		System.out.println(a);
		
		Provider p1 = new BouncyCastleProvider();
		Provider p2 = new BouncyCastlePQCProvider();
		
		String pString1 = "BC";
		String pString2 = "BCPQC";
//		MessageDigest md = MessageDigest.getInstance("AES", BouncyCastleProvider.PROVIDER_NAME);
		
//		if(n%2==0) {
//			pString1 = "BC";
//		}
//		else {
//			pString1 = "BCPQC";
//		}
		
//		switch(n) {
//			case 0:
//				pString1="A";
//				break;
//			case 1: 
//				pString1="B";
//				break;
//			case 2:
//				pString1="C";
//				break;
//			default:
//				pString1="D";
//				break;
//		}
		
		KeyGenerator keygenerator = KeyGenerator.getInstance("AES", pString1);
		keygenerator.generateKey();
//		Provider p = keygenerator.getProvider();
//		System.out.println(p.getInfo());
		
		//((n%2==0) ? p1 : p2)
		//((n%2==0) ? pString1 : pString2)
	}
}
