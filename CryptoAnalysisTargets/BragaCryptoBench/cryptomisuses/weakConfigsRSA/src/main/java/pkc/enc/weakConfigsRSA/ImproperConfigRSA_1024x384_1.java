
package pkc.enc.weakConfigsRSA;

import javax.crypto.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.spec.*;
import javax.crypto.spec.PSource;
import org.bouncycastle.jce.provider.*;

public final class ImproperConfigRSA_1024x384_1 {

	/**
	 * Original test with updated constraints:
	 * 	MGF1ParameterSpec.SHA384 -> new MGF1ParameterSpec("SHA-384")
	 */
	public void positiveTestCase() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException,
										IllegalBlockSizeException, NoSuchProviderException, InvalidAlgorithmParameterException {
		Security.addProvider(new BouncyCastleProvider());

		int ksize = 1024;
		int hsize = 384;
		int maxLenBytes = (ksize - 2 * hsize) / 8 - 2;

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
		kpg.initialize(ksize);
		KeyPair kp = kpg.generateKeyPair();

		MGF1ParameterSpec mgf1ps = new MGF1ParameterSpec("SHA-384");
		OAEPParameterSpec OAEPps = new OAEPParameterSpec("SHA-384", "MGF1", mgf1ps, PSource.PSpecified.DEFAULT);
		Cipher c = Cipher.getInstance("RSA/None/OAEPPadding", "BC");

		c.init(Cipher.ENCRYPT_MODE, kp.getPublic(), OAEPps);
		byte[] pt1 = "demo text".substring(0, maxLenBytes).getBytes();
		byte[] ct = c.doFinal(pt1);

		c.init(Cipher.DECRYPT_MODE, kp.getPrivate(), OAEPps);
		byte[] pt2 = c.doFinal(ct);
	}

	/**
	 * Original test without updates
	 */
	public void negativeTestCase() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException,
										IllegalBlockSizeException, NoSuchProviderException, InvalidAlgorithmParameterException {
		Security.addProvider(new BouncyCastleProvider());

		int ksize = 1024;
		int hsize = 384;
		int maxLenBytes = (ksize - 2 * hsize) / 8 - 2;

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
		kpg.initialize(ksize);
		KeyPair kp = kpg.generateKeyPair();

		// Since 3.0.0: MGF1ParameterSpec should use the constructor
		MGF1ParameterSpec mgf1ps = MGF1ParameterSpec.SHA384;
		OAEPParameterSpec OAEPps = new OAEPParameterSpec("SHA-384", "MGF1", mgf1ps, PSource.PSpecified.DEFAULT);
		Cipher c = Cipher.getInstance("RSA/None/OAEPPadding", "BC");

		c.init(Cipher.ENCRYPT_MODE, kp.getPublic(), OAEPps);
		byte[] pt1 = "demo text".substring(0, maxLenBytes).getBytes();
		byte[] ct = c.doFinal(pt1);

		c.init(Cipher.DECRYPT_MODE, kp.getPrivate(), OAEPps);
		byte[] pt2 = c.doFinal(ct);
	}
}
