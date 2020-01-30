
package pdf.insecureComboMacEnc;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

public final class ManualComboMACThenEncrypt {

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

		String s = "MAC-then-Encrypt (MtE)";
		m.init(sks2);
		c.init(Cipher.ENCRYPT_MODE, k1, new IvParameterSpec(iv));
		byte[] tag = m.doFinal(pt1);
		byte[] pack = Arrays.concatenate(pt1, tag);
		byte[] ct = c.doFinal(pack);

		m.init(sks2);
		c.init(Cipher.DECRYPT_MODE, k1, new IvParameterSpec(iv));
		pack = c.doFinal(ct);
		byte[] ptBeto = Arrays.copyOfRange(pack, 0, 16);
		tag = Arrays.copyOfRange(pack, 16, pack.length);
		ok = MessageDigest.isEqual(m.doFinal(ptBeto), tag);
	}
}
