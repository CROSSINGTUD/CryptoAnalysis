package example;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class UseOAEPForRSA {

	/**
	 * Original test with updated constraints
	 * 	kpg.initialize(2048) -> kpg.initialize(4096)
	 */
	public void positiveTestCase() {
		try {
			Security.addProvider(new BouncyCastleProvider());
			byte[] ptA = ("Randomized RSA").getBytes();
	  
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
			kpg.initialize(4096);
			KeyPair kp = kpg.generateKeyPair();
			Cipher enc = Cipher.getInstance("RSA/None/OAEPWithSHA256AndMGF1Padding", "BC");
			enc.init(Cipher.ENCRYPT_MODE, kp.getPublic());
			Cipher dec = Cipher.getInstance("RSA/None/OAEPWithSHA256AndMGF1Padding", "BC");
			dec.init(Cipher.DECRYPT_MODE, kp.getPrivate());
	  
			byte[][] ct = new byte[2][];
			for (int i = 0; i < 2; i++) {
			  ct[i] = enc.doFinal(ptA);
			  byte[] ptB = dec.doFinal(ct[i]);
			}
		  } catch (NoSuchAlgorithmException | NoSuchPaddingException |
				  InvalidKeyException | IllegalBlockSizeException |
				  BadPaddingException | NoSuchProviderException e) {}
	}

	/**
	 * Original test without any updates
	 */
	public void negativeTestCase() {
		try {
			Security.addProvider(new BouncyCastleProvider());
			byte[] ptA = ("Randomized RSA").getBytes();
	  
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");

			// Since 3.0.0: key size of 2048 is not allowed
			kpg.initialize(2048);
			KeyPair kp = kpg.generateKeyPair();
			Cipher enc = Cipher.getInstance("RSA/None/OAEPWithSHA256AndMGF1Padding", "BC");
			enc.init(Cipher.ENCRYPT_MODE, kp.getPublic());
			Cipher dec = Cipher.getInstance("RSA/None/OAEPWithSHA256AndMGF1Padding", "BC");
			dec.init(Cipher.DECRYPT_MODE, kp.getPrivate());
	  
			byte[][] ct = new byte[2][];
			for (int i = 0; i < 2; i++) {
			  ct[i] = enc.doFinal(ptA);
			  byte[] ptB = dec.doFinal(ct[i]);
			}
		  } catch (NoSuchAlgorithmException | NoSuchPaddingException |
				  InvalidKeyException | IllegalBlockSizeException |
				  BadPaddingException | NoSuchProviderException e) {}
	}
}
