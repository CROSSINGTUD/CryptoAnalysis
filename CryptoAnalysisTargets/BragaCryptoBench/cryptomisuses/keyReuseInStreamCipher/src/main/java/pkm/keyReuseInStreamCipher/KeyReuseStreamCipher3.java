
package pkm.keyReuseInStreamCipher;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class KeyReuseStreamCipher3 {

	public static void main(String args[]) {
		try {
			Security.addProvider(new BouncyCastleProvider());
			byte[][] M = { ("key1").getBytes(), ("key2").getBytes() };
			byte[] iv1 = new byte[] { (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xAB,
					(byte) 0xCD, (byte) 0xEF, (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89,
					(byte) 0xAB, (byte) 0xCD, (byte) 0xEF };
			byte[] iv2 = new byte[] { (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xAB,
					(byte) 0xCD, (byte) 0xEF, (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89,
					(byte) 0xAB, (byte) 0xCD, (byte) 0xEF };

			byte[] k = new byte[16];
			(new SecureRandom()).nextBytes(k);
			byte[][] C = new byte[2][];

			SecretKeySpec ks = new SecretKeySpec(k, "AES");
			Cipher enc = Cipher.getInstance("AES/OFB/NoPadding", "BC");

			enc.init(Cipher.ENCRYPT_MODE, ks, new IvParameterSpec(iv1));
			C[0] = enc.doFinal(M[0]);
			enc.init(Cipher.ENCRYPT_MODE, ks, new IvParameterSpec(iv2));
			C[1] = enc.doFinal(M[1]);

		} catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
		}
	}
}
