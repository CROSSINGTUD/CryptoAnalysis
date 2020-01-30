package ivm.staticCounterCTR;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class StaticCounterCTR1 {

	public static void main(String args[])
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException,
			IllegalBlockSizeException, NoSuchProviderException, InvalidAlgorithmParameterException {

		Security.addProvider(new BouncyCastleProvider());
		byte[] pt1 = ("static counter").getBytes();

		byte[] ictr = new byte[] { (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xAB,
				(byte) 0xCD, (byte) 0xEF, (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xAB,
				(byte) 0xCD, (byte) 0xEF };

		KeyGenerator g = KeyGenerator.getInstance("AES", "BC");
		g.init(128);
		Key k = g.generateKey();

		Cipher enc = Cipher.getInstance("AES/CTR/NoPadding", "BC");
		Cipher dec = Cipher.getInstance("AES/CTR/NoPadding", "BC");
		byte[] ct;

		enc.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(ictr));
		ct = enc.doFinal(pt1);
		byte[] ctr = enc.getIV();
		dec.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(ctr));
		byte[] pt2 = dec.doFinal(ct);

		enc.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(ictr));
		ct = enc.doFinal(pt1);
		ctr = enc.getIV();
		dec.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(ctr));
		pt2 = dec.doFinal(ct);

	}
}
