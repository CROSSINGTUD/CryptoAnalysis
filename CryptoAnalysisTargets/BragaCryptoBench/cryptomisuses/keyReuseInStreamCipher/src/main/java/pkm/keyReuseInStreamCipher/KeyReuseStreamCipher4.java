
package pkm.keyReuseInStreamCipher;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class KeyReuseStreamCipher4 {

	public static void main(String args[]) {
		try {
			Security.addProvider(new BouncyCastleProvider());
			byte[][] M = { ("key1").getBytes(), ("key2").getBytes() };
			byte[] seed = new byte[] { (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xAB,
					(byte) 0xCD, (byte) 0xEF, (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89,
					(byte) 0xAB, (byte) 0xCD, (byte) 0xEF };

			SecureRandom sr = new SecureRandom();
			sr.setSeed(seed);

			byte[] iv = new byte[16];
			byte[] k = new byte[16];

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
