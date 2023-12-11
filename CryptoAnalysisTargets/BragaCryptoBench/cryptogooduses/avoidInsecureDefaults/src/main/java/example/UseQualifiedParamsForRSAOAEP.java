package example;

import javax.crypto.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.spec.*;
import javax.crypto.spec.PSource;
import org.bouncycastle.jce.provider.*;

public final class UseQualifiedParamsForRSAOAEP {

	/**
	* Original test with updated constraints:
	* 	int ksize = 2048 -> int ksize = 4096
	*	MGF1ParameterSpec.SHA1 -> new MGF1ParameterSpec("SHA1")
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

		MGF1ParameterSpec mgf1ps = new MGF1ParameterSpec("SHA1");
		OAEPParameterSpec OAEPps = new OAEPParameterSpec("SHA-256", "MGF1", mgf1ps, PSource.PSpecified.DEFAULT);
		Cipher c = Cipher.getInstance("RSA/None/OAEPPadding", "BC");

		Key pubk = kp.getPublic();
		c.init(Cipher.ENCRYPT_MODE, pubk, OAEPps);
		byte[] ptA = "This is a demo text".substring(0, maxLenBytes).getBytes();
		byte[] ct = c.doFinal(ptA);

		Key privk = kp.getPrivate();
		c.init(Cipher.DECRYPT_MODE, privk, OAEPps);
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

		// Since 3.0.0: MGF1ParameterSpec should use the constructor and SHA1 is not allowed
		MGF1ParameterSpec mgf1ps = MGF1ParameterSpec.SHA1;
		OAEPParameterSpec OAEPps = new OAEPParameterSpec("SHA-256", "MGF1", mgf1ps, PSource.PSpecified.DEFAULT);
		Cipher c = Cipher.getInstance("RSA/None/OAEPPadding", "BC");

		Key pubk = kp.getPublic();
		c.init(Cipher.ENCRYPT_MODE, pubk, OAEPps);
		byte[] ptA = "This is a demo text".substring(0, maxLenBytes).getBytes();
		byte[] ct = c.doFinal(ptA);

		Key privk = kp.getPrivate();
		c.init(Cipher.DECRYPT_MODE, privk, OAEPps);
		byte[] ptB = c.doFinal(ct);
	}
}