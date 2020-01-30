
package pdf.insecureComboHashEnc;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

public final class ManualComboHashThenEncrypt {

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

		String s = "Hash-then-Encrypt(HtE)";
		md.reset();
		c.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv));
		byte[] hash = md.digest(pt1);
		byte[] concatArrays = Arrays.concatenate(pt1, hash);
		byte[] ct = c.doFinal(concatArrays);

		if (ivo) {
			X = "demo text 2".getBytes();
			byte[] cryptoText = Arrays.copyOfRange(ct, 0, 16);
			byte[] cryptoTag = Arrays.copyOfRange(ct, 16, ct.length);
			md.reset();
			byte[] t1 = md.digest("digested1".getBytes());
			md.reset();
			byte[] t2 = md.digest("digested2".getBytes());
			Y = "demo text 3".getBytes();
			ct = Arrays.concatenate(cryptoText, cryptoTag);
		}
		md.reset();
		c.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv));
		concatArrays = c.doFinal(ct);
		byte[] pt2 = Arrays.copyOfRange(concatArrays, 0, 16);
		hash = Arrays.copyOfRange(concatArrays, 16, concatArrays.length);
		ok = MessageDigest.isEqual(md.digest(pt2), hash);
	}
}
