package pdf.sideChannelAttacks;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class PaddingOracle {

	private static final byte[] text = ("demo text").getBytes();

	private static final byte[] k = "00112233445566778899AABBCCDDEEFF".getBytes();
	public static final byte[] iv = "0123456789ABCDEF0123456789ABCDEF".getBytes();
	private static final SecretKeySpec ks = new SecretKeySpec(k, "AES");
	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static boolean oracle(byte[] iv, byte[] c) {
		boolean ok = true;
		try {
			Cipher enc = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
			enc.init(Cipher.DECRYPT_MODE, ks, new IvParameterSpec(iv));
			enc.doFinal(c);
		} catch (BadPaddingException e) {
			ok = false;
		} catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException ex) {
		}
		return ok;
	}

	public static byte[] encripta() {
		byte[] encrypted = null;
		try {
			Cipher enc = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
			enc.init(Cipher.ENCRYPT_MODE, ks, new IvParameterSpec(iv));
			encrypted = enc.doFinal(text);
		} catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException
				| NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
		}
		return encrypted;
	}

}
