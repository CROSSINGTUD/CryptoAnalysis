
package pdf.insecureComboHashEnc;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

public final class ManualComboEncryptThenHash2 {

	public static void main(String args[])
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException,
			IllegalBlockSizeException, NoSuchProviderException, InvalidAlgorithmParameterException {

		Security.addProvider(new BouncyCastleProvider());

		byte[] iv = new byte[16];
		KeyGenerator g = KeyGenerator.getInstance("AES", "BC");
		g.init(256);
		Key k = g.generateKey();
		(new SecureRandom()).nextBytes(iv);
		Cipher c = Cipher.getInstance("AES/CTR/NoPadding", "BC");
		MessageDigest md = MessageDigest.getInstance("SHA256", "BC");
		byte[] pt1 = "demo text".getBytes(), X, Y;
		boolean ok, ivo = true;

		String s = "Encrypt-then-Hash(EtH)";
		md.reset();
		c.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv));
		byte[] ct = c.doFinal(pt1);
		byte[] ctPlusIV = ct.clone();
		Arrays.concatenate(ctPlusIV, iv);
		byte[] hash = md.digest(ctPlusIV);

		if (ivo) {
			X = "demo text 1".getBytes();
			ct = "demo text 2".getBytes();
			hash = md.digest(ct);
		}

		md.reset();
		c.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv));
		byte[] pt2 = c.doFinal(ct);
		ctPlusIV = ct.clone();
		Arrays.concatenate(ctPlusIV, iv);
		ok = MessageDigest.isEqual(md.digest(ctPlusIV), hash);
	}
}
