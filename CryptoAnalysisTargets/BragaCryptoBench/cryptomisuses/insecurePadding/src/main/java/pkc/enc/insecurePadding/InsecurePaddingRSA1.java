package pkc.enc.insecurePadding;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class InsecurePaddingRSA1 {

	/**
	 * Original test with updated constraints:
	 * 	kg.initialize(2048, ...) -> kg.initialize(4096, ...)
	 */
	public void positiveTestCase() {
		try {
			Security.addProvider(new BouncyCastleProvider());
			byte[] msg1 = ("demo msg").getBytes();
			KeyPairGenerator g = KeyPairGenerator.getInstance("RSA", "BC");
			g.initialize(4096);
			KeyPair kp = g.generateKeyPair();

			Cipher enc = Cipher.getInstance("RSA/ECB/NoPadding", "BC");
			enc.init(Cipher.ENCRYPT_MODE, kp.getPublic());
			Cipher dec = Cipher.getInstance("RSA/ECB/NoPadding", "BC");
			dec.init(Cipher.DECRYPT_MODE, kp.getPrivate());

			byte[][] ct = new byte[2][];
			for (int i = 0; i < 2; i++) {
				ct[i] = enc.doFinal(msg1);
				byte[] deciphered = dec.doFinal(ct[i]);
			}

		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException | NoSuchProviderException e) {
		}
	}

	/**
	 * Original test without updates
	 */
	public void negativeTestCase() {
		try {
			Security.addProvider(new BouncyCastleProvider());
			byte[] msg1 = ("demo msg").getBytes();
			KeyPairGenerator g = KeyPairGenerator.getInstance("RSA", "BC");

			// Since 3.0.0: key size of 2048 is not allowed
			g.initialize(2048);
			KeyPair kp = g.generateKeyPair();

			Cipher enc = Cipher.getInstance("RSA/ECB/NoPadding", "BC");
			enc.init(Cipher.ENCRYPT_MODE, kp.getPublic());
			Cipher dec = Cipher.getInstance("RSA/ECB/NoPadding", "BC");
			dec.init(Cipher.DECRYPT_MODE, kp.getPrivate());

			byte[][] ct = new byte[2][];
			for (int i = 0; i < 2; i++) {
				ct[i] = enc.doFinal(msg1);
				byte[] deciphered = dec.doFinal(ct[i]);
			}

		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException | NoSuchProviderException e) {
		}
	}
}
