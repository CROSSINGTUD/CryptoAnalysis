package issue70;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ClientProtocolDecoder {

	public static final byte[] AES_KEY = "vWf7g1Gt701h0.#0".getBytes();
	public static final byte[] AES_IV = "rgnHV16#8HQFc&16".getBytes();
	public ClientProtocolDecoder() {
	}

	public static byte[] decryptAES(byte[] bytes) throws Exception {
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			SecretKeySpec keyspec = new SecretKeySpec(AES_KEY, "AES");
			IvParameterSpec ivspec = new IvParameterSpec(AES_IV);
			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
			byte[] original = cipher.doFinal(bytes);
			return original;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
