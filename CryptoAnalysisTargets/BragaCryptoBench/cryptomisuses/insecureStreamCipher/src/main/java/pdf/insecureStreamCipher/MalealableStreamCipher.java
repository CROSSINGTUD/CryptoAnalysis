package pdf.insecureStreamCipher;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class MalealableStreamCipher {

	public static void main(String args[]) {
		try {
			Security.addProvider(new BouncyCastleProvider());
			byte[][] M = { ("msg1").getBytes(), ("msg2").getBytes() };
			byte[][] iv = { "0123456789ABCDEF0123456789ABCDEF".getBytes(),
					"0123456789ABCDEF0123456789ABCDEF".getBytes() };
			byte[][] iv2 = { iv[0].clone(), iv[0].clone() };
			byte[] k = "00112233445566778899AABBCCDDEEFF".getBytes(), X = null;
			byte[][] C = new byte[2][], N = new byte[2][];

			SecretKeySpec ks = new SecretKeySpec(k, "AES");
			Cipher c = Cipher.getInstance("AES/CTR/NoPadding", "BC");

			for (int i = 0; i < M.length; i++) {
				c.init(Cipher.ENCRYPT_MODE, ks, new IvParameterSpec(iv[i]));
				C[i] = c.doFinal(M[i]);
			}

			for (int i = 0; i < C.length; i++) {
				c.init(Cipher.DECRYPT_MODE, ks, new IvParameterSpec(iv2[i]));
				N[i] = c.doFinal(C[i]);
			}
		} catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
		}
	}
}
