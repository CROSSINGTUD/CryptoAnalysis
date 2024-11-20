package pkc.sign.weakSignatureRSA;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class PSSw1024xSHA256_2 {

	/**
	 * Original test with updated constraints:
	 * 	kg.initialize(2048, ...) -> kg.initialize(4096, ...)
	 */
	public void positiveTestCase() throws Exception {
		Security.addProvider(new BouncyCastleProvider());

		KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA", "BC");
		kg.initialize(4096, new SecureRandom());
		KeyPair kp = kg.generateKeyPair();
		Signature sig = Signature.getInstance("SHA256withRSAandMGF1", "BC");
		PSSParameterSpec spec = new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 20, 1);
		sig.setParameter(spec);

		byte[] m = "Testing weak RSA-PSS".getBytes("UTF-8");

		sig.initSign(kp.getPrivate(), new SecureRandom());
		sig.update(m);
		byte[] s = sig.sign();

		sig.initVerify(kp.getPublic());
		sig.update(m);
	}

	/**
	 * Original test without updates
	 */
	public void negativeTestCase() throws Exception {
		Security.addProvider(new BouncyCastleProvider());

		KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA", "BC");

		// Since 3.0.0: key size of 2048 is not allowed
		kg.initialize(2048, new SecureRandom());
		KeyPair kp = kg.generateKeyPair();
		Signature sig = Signature.getInstance("SHA256withRSAandMGF1", "BC");
		PSSParameterSpec spec = new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 20, 1);
		sig.setParameter(spec);

		byte[] m = "Testing weak RSA-PSS".getBytes("UTF-8");

		sig.initSign(kp.getPrivate(), new SecureRandom());
		sig.update(m);
		byte[] s = sig.sign();

		sig.initVerify(kp.getPublic());
		sig.update(m);
	}
}