
package pdf.insecureComboHashEnc;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class ManualComboEncryptAndHash {

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

		String s = "Encrypt-and-Hash (E&H)";
		md.reset();
		c.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv));
		byte[] ct = c.doFinal(pt1);
		byte[] hash = md.digest(pt1);

		if (ivo) {
			X = "demo text 1".getBytes();
			ct = "demo text 2".getBytes();
			hash = md.digest("digested text".getBytes());
		}

		md.reset();
		c.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv));
		byte[] pt2 = c.doFinal(ct);
		ok = MessageDigest.isEqual(md.digest(pt2), hash);
	}

}
