package pkc.enc.deterministicCrypto;

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

public final class DeterministicEncryptionRSA {

	public static void main(String args[]) {
		try {
			Security.addProvider(new BouncyCastleProvider());
			byte[] text1 = ("demo text").getBytes();
			KeyPairGenerator g = KeyPairGenerator.getInstance("RSA", "BC");
			g.initialize(512);
			KeyPair kp = g.generateKeyPair();
			String[] rsa = { "RSA", "RSA/ECB/NoPadding", "RSA/None/NoPadding", "RSA/None/PKCS1Padding",
					"RSA/None/OAEPWithSHA1AndMGF1Padding" };
			for (int a = 0; a < rsa.length; a++) {
				Cipher enc = Cipher.getInstance(rsa[a], "BC");
				enc.init(Cipher.ENCRYPT_MODE, kp.getPublic());
				Cipher dec = Cipher.getInstance(rsa[a], "BC");
				dec.init(Cipher.DECRYPT_MODE, kp.getPrivate());

				byte[][] bytearr = new byte[2][];
				for (int i = 0; i < 2; i++) {
					bytearr[i] = enc.doFinal(text1);
					byte[] text2 = dec.doFinal(bytearr[i]);
				}
			}
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException | NoSuchProviderException e) {
		}
	}
}
