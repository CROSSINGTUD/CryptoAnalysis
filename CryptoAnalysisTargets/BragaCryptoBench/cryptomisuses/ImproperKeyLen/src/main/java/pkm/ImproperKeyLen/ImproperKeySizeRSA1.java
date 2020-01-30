package pkm.ImproperKeyLen;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public final class ImproperKeySizeRSA1 {

	public static void main(String args[]) {

		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "SunJSSE");
			kpg.initialize(512);
			KeyPair kp = kpg.generateKeyPair();
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
		}
	}
}
