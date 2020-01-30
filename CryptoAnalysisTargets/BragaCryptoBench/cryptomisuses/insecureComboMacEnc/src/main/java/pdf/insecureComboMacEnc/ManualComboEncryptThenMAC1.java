package pdf.insecureComboMacEnc;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class ManualComboEncryptThenMAC1 {

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
		byte[] text1 = "demo text".getBytes(), X;
		boolean ok, ivo = false;

		String s = "Encrypt-then-MAC(EtM)";
		m.init(sks2);
		c.init(Cipher.ENCRYPT_MODE, k1, new IvParameterSpec(iv));
		byte[] ciphered = c.doFinal(text1);
		byte[] tag = m.doFinal(ciphered);

		if (ivo) {
			X = "demo text 1".getBytes();
			ciphered = "demo text 2".getBytes();
		}

		ok = MessageDigest.isEqual(m.doFinal(ciphered), tag);
		if (ok) {
			m.init(sks2);
			c.init(Cipher.DECRYPT_MODE, k1, new IvParameterSpec(iv));
			byte[] textoclaroBeto = c.doFinal(ciphered);
		}
	}
}
