package pdf.insecureComboMacEnc;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class ManualComboEncryptAndMAC {

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
		byte[] pt1 = "demo text".getBytes(), X;
		boolean ok, ivo = false;

		String s = "Encrypt-and-MAC (E&M)";
		m.init(sks2);
		c.init(Cipher.ENCRYPT_MODE, k1, new IvParameterSpec(iv));
		byte[] ct = c.doFinal(pt1);
		byte[] tag = m.doFinal(pt1);

		if (ivo) {
			X = "demo text 1".getBytes();
			ct = "demo text 2".getBytes();
		}

		m.init(sks2);
		c.init(Cipher.DECRYPT_MODE, k1, new IvParameterSpec(iv));
		byte[] pt2 = c.doFinal(ct);
		ok = MessageDigest.isEqual(m.doFinal(pt2), tag);
	}
}
