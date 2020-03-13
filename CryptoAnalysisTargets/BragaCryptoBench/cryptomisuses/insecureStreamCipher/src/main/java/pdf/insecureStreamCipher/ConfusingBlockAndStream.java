package pdf.insecureStreamCipher;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class ConfusingBlockAndStream {

	public static void main(String args[]) {
		try {
			Security.addProvider(new BouncyCastleProvider());
			byte[][] M = { ("msg1").getBytes(), ("msg2").getBytes() };
			byte[][] C = new byte[2][], iv = new byte[2][];

			byte[] k = "00112233445566778899AABBCCDDEEFF".getBytes();
			byte[] seed = "0123456789ABCDEF0123456789ABCDEF".getBytes();

			SecretKeySpec ks = new SecretKeySpec(k, "AES");
			Cipher enc = Cipher.getInstance("AES/OFB/NoPadding", "BC");

			SecureRandom sr = new SecureRandom();
			sr.setSeed(seed);
			enc.init(Cipher.ENCRYPT_MODE, ks, sr);
			C[0] = enc.doFinal(M[0]);
			iv[0] = enc.getIV();

			sr = new SecureRandom();
			sr.setSeed(seed);
			enc.init(Cipher.ENCRYPT_MODE, ks, sr);
			C[1] = enc.doFinal(M[1]);
			iv[1] = enc.getIV();

		} catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException ex) {
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
		}
	}
}
