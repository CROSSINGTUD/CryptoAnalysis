package example;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


public final class PSSwSHA256Signature {

	/**
	* Original test with updated constraints
	*	kg.initialize(2048, new SecureRandom()) -> kg.initialize(4096, new SecureRandom());
	*/
	public void positiveTestCase() throws Exception {
		Security.addProvider(new BouncyCastleProvider());

		KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA", "BC");
		kg.initialize(4096, new SecureRandom());
		KeyPair kp = kg.generateKeyPair();
		Signature sig = Signature.getInstance("SHA256withRSAandMGF1", "BC");
	
		byte[] m = "Testing RSA PSS w/ SHA256".getBytes("UTF-8");
		
		sig.initSign(kp.getPrivate(), new SecureRandom());
		sig.update(m);
		byte[] s = sig.sign();
	
		sig.initVerify(kp.getPublic());
		sig.update(m);
	}

	/**
	 * Original test case without any changes
	 */
	public void negativeTestCase() throws Exception {
		Security.addProvider(new BouncyCastleProvider());

		KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA", "BC");
		kg.initialize(2048, new SecureRandom());
		KeyPair kp = kg.generateKeyPair();
		Signature sig = Signature.getInstance("SHA256withRSAandMGF1", "BC");
	
		byte[] m = "Testing RSA PSS w/ SHA256".getBytes("UTF-8");
		
		sig.initSign(kp.getPrivate(), new SecureRandom());
		sig.update(m);
		byte[] s = sig.sign();
	
		sig.initVerify(kp.getPublic());
		sig.update(m);
	}
}
