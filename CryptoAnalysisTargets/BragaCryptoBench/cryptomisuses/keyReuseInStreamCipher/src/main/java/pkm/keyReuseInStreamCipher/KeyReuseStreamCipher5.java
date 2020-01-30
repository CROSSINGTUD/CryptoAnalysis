
package pkm.keyReuseInStreamCipher;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

//
public final class KeyReuseStreamCipher5 {

	public static void main(String args[]) {
		try {
			Security.addProvider(new BouncyCastleProvider());
			byte[][] M = { ("key1").getBytes(), ("key2").getBytes() };

			SecureRandom sr = new SecureRandom();

			byte[] iv = new byte[16], k = new byte[16];

			sr.nextBytes(k);
			sr.nextBytes(iv);

			byte[][] C = new byte[2][];

			SecretKeySpec ks = new SecretKeySpec(k, "AES");
			Cipher enc = Cipher.getInstance("AES/OFB/NoPadding", "BC");

			enc.init(Cipher.ENCRYPT_MODE, ks, new IvParameterSpec(iv));
			C[0] = enc.doFinal(M[0]);
			enc.init(Cipher.ENCRYPT_MODE, ks, new IvParameterSpec(iv));
			C[1] = enc.doFinal(M[1]);

		} catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
		}
	}
}
