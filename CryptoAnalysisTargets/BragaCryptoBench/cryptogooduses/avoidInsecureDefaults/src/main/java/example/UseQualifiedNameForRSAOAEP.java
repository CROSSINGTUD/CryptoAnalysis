package example;

import javax.crypto.*;
import java.security.*;
import org.bouncycastle.jce.provider.*;

public final class UseQualifiedNameForRSAOAEP {

	/**
	* Original test with updated constraints:
	* 	int ksize = 2048 -> int ksize = 4096
	*/
	public void positiveTestCase() throws NoSuchAlgorithmException,
  									NoSuchPaddingException, InvalidKeyException, BadPaddingException,
  									IllegalBlockSizeException, NoSuchProviderException,
  									InvalidAlgorithmParameterException {
		Security.addProvider(new BouncyCastleProvider());

		int ksize = 4096;
		int hsize = 256; 
		int maxLenBytes = (ksize - 2 * hsize) / 8 - 2;

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
		kpg.initialize(ksize);
		KeyPair kp = kpg.generateKeyPair(); 

		Cipher c = Cipher.getInstance("RSA/None/OAEPwithSHA256andMGF1Padding","BC");
	
		Key pubk = kp.getPublic();
		c.init(Cipher.ENCRYPT_MODE, pubk);
		byte[] ptA = "This is a demo text".substring(0, maxLenBytes).getBytes();
		byte[] ct = c.doFinal(ptA);

		Key privk = kp.getPrivate();
		c.init(Cipher.DECRYPT_MODE, privk);
		byte[] ptB = c.doFinal(ct);
	}

	/**
	 * Original test without any updates
	 */
	public void negativeTestCase() throws NoSuchAlgorithmException,
									NoSuchPaddingException, InvalidKeyException, BadPaddingException,
									IllegalBlockSizeException, NoSuchProviderException,
									InvalidAlgorithmParameterException {
		Security.addProvider(new BouncyCastleProvider());

		// Since 3.0.0: key size of 2048 is not allowed
		int ksize = 2048;
		int hsize = 256; 
		int maxLenBytes = (ksize - 2 * hsize) / 8 - 2;

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
		kpg.initialize(ksize);
		KeyPair kp = kpg.generateKeyPair(); 

		Cipher c = Cipher.getInstance("RSA/None/OAEPwithSHA256andMGF1Padding","BC");

		Key pubk = kp.getPublic();
		c.init(Cipher.ENCRYPT_MODE, pubk);
		byte[] ptA = "This is a demo text".substring(0, maxLenBytes).getBytes();
		byte[] ct = c.doFinal(ptA);

		Key privk = kp.getPrivate();
		c.init(Cipher.DECRYPT_MODE, privk);
		byte[] ptB = c.doFinal(ct);
	}
}
