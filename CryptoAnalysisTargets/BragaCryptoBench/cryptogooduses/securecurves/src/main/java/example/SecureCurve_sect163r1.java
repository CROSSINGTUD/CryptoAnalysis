package example;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.ECGenParameterSpec;

public final class SecureCurve_sect163r1 {

	/**
	 * Original test with updated constraint:
	 * 	new ECGenParameterSpec("sect163r1") -> new ECGenParameterSpec("secp521r1")
	 */
	public void positiveTestCase() {
		try {
			ECGenParameterSpec ecps = new ECGenParameterSpec("secp521r1");

			KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", "SunEC");
			kpg.initialize(ecps);
			KeyPair kp = kpg.generateKeyPair();

		} catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
		}
	}

	/**
	 * Original test without any updates
	 */
	public void negativeTestCase() {
		try {
			// Since 3.0.0: sect163r1 is not allowed
			ECGenParameterSpec ecps = new ECGenParameterSpec("sect163r1");

			KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", "SunEC");
			kpg.initialize(ecps);
			KeyPair kp = kpg.generateKeyPair();

		} catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
		}
	}
}
