
package pkc.enc.weakConfigsRSA;

import javax.crypto.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.spec.*;
import javax.crypto.spec.PSource;
import org.bouncycastle.jce.provider.*;

public final class ImproperConfigRSA_768x160_1 {

	/**
	 * Original test with updated constraints:
	 * 	MGF1ParameterSpec.SHA1 -> new MGF1ParameterSpec("SHA-512")
	 * 	new OAEPParameterSpec("SHA1", "MGF1", mgf1ps, PSource.PSpecified.DEFAULT) -> new OAEPParameterSpec("SHA-512", "MGF1", mgf1ps, PSource.PSpecified.DEFAULT)
	 * 	int hsize = 160 -> int hsize = 512
	 */
	public void positiveTestCase() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException,
									IllegalBlockSizeException, NoSuchProviderException, InvalidAlgorithmParameterException {
		Security.addProvider(new BouncyCastleProvider());

		int ksize = 768;
		int hsize = 512;
		int maxLenBytes = (ksize - 2 * hsize) / 8 - 2;

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
		kpg.initialize(ksize);
		KeyPair kp = kpg.generateKeyPair();

		MGF1ParameterSpec mgf1ps = new MGF1ParameterSpec("SHA-512");
		OAEPParameterSpec OAEPps = new OAEPParameterSpec("SHA-512", "MGF1", mgf1ps, PSource.PSpecified.DEFAULT);
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

		int ksize = 768;
		int hsize = 160;
		int maxLenBytes = (ksize - 2 * hsize) / 8 - 2;

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
		kpg.initialize(ksize);
		KeyPair kp = kpg.generateKeyPair();

		// Since 3.0.0: MGF1ParameterSpec should use the constructor and SHA1 is not allowed for both specs
		MGF1ParameterSpec mgf1ps = MGF1ParameterSpec.SHA1;
		OAEPParameterSpec OAEPps = new OAEPParameterSpec("SHA1", "MGF1", mgf1ps, PSource.PSpecified.DEFAULT);
		Cipher c = Cipher.getInstance("RSA/None/OAEPPadding", "BC");

		c.init(Cipher.ENCRYPT_MODE, kp.getPublic(), OAEPps);
		byte[] pt1 = "demo text".substring(0, maxLenBytes).getBytes();
		byte[] ct = c.doFinal(pt1);

		c.init(Cipher.DECRYPT_MODE, kp.getPrivate(), OAEPps);
		byte[] pt2 = c.doFinal(ct);
	}
}
