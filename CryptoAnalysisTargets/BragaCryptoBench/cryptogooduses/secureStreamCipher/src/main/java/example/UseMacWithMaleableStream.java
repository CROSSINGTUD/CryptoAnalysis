
package example;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

public final class UseMacWithMaleableStream {

	public static void main(String args[])
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException,
			IllegalBlockSizeException, NoSuchProviderException, InvalidAlgorithmParameterException {

		Security.addProvider(new BouncyCastleProvider());

		byte[] iv = new byte[16], k2 = new byte[16];
		KeyGenerator g = KeyGenerator.getInstance("AES", "BC");
		g.init(256);
		Key k1 = g.generateKey();
		(new SecureRandom()).nextBytes(iv);
		(new SecureRandom()).nextBytes(k2);
		SecretKeySpec sks2 = new SecretKeySpec(k2, "HMACSHA256");
		Cipher c = Cipher.getInstance("AES/CTR/NoPadding", "BC");
		Mac m = Mac.getInstance("HMACSHA256", "BC");
		byte[] pt1 = "text demo".getBytes(), X;
		boolean ok, ivo = true;

		String s = "Encrypt-then-MAC(EtM): calcula a tag do criptograma";
		m.init(sks2);
		c.init(Cipher.ENCRYPT_MODE, k1, new IvParameterSpec(iv));
		byte[] ct = c.doFinal(pt1);
		byte[] ctPlusIV = ct.clone();
		Arrays.concatenate(ctPlusIV, iv);
		byte[] tag = m.doFinal(ctPlusIV);

		if (ivo) {
			X = "demo text 2".getBytes();
			ct = "demo text 3".getBytes();
		}

		ctPlusIV = ct.clone();
		Arrays.concatenate(ctPlusIV, iv);
		ok = MessageDigest.isEqual(m.doFinal(ctPlusIV), tag);
		if (ok) {
			m.init(sks2);
			c.init(Cipher.DECRYPT_MODE, k1, new IvParameterSpec(iv));
			byte[] ptBeto = c.doFinal(ct);
		}
	}
}
