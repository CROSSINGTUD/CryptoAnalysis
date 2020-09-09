package example;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.ECGenParameterSpec;

public final class SecureCurve_sect163r2 {

	public static void main(String argv[]) {
		try {
			ECGenParameterSpec ecps = new ECGenParameterSpec("sect163r2");

			KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", "SunEC");
			kpg.initialize(ecps);
			KeyPair kp = kpg.generateKeyPair();

		} catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
		}
	}
}
