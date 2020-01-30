
package ivm.nonceReuse;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

//
public final class NonceReuse1 {

	public static void main(String args[]) {
		try {
			Security.addProvider(new BouncyCastleProvider());
			byte[][] M = { ("Hidden part.....").getBytes(), ("Revealed part...").getBytes() };

			SecureRandom sr = SecureRandom.getInstanceStrong();
			byte[] iv = new byte[16], k = new byte[16];
			byte[][] C = new byte[2][];

			sr.nextBytes(k);
			SecretKeySpec ks = new SecretKeySpec(k, "AES");
			Cipher enc = Cipher.getInstance("AES/OFB/NoPadding", "BC");

			sr.nextBytes(iv);
			enc.init(Cipher.ENCRYPT_MODE, ks, new IvParameterSpec(iv));
			C[0] = enc.doFinal(M[0]);
			enc.init(Cipher.ENCRYPT_MODE, ks, new IvParameterSpec(iv));
			C[1] = enc.doFinal(M[1]);

		} catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
		}
	}
}
