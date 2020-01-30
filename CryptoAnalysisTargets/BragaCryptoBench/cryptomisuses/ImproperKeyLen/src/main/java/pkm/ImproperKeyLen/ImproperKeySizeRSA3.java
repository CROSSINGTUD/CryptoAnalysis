package pkm.ImproperKeyLen;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class ImproperKeySizeRSA3 {

	public static void main(String args[]) {

		try {
			Security.addProvider(new BouncyCastleProvider());
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
			kpg.initialize(384);
			KeyPair kp = kpg.generateKeyPair();

		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
		}
	}
}
