package pdf.insecureDefault;

import javax.crypto.*;
import java.security.*;

public final class InsecureDefaultAES {

	public static void main(String args[]) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
		byte[] msg = "demo msg".getBytes();
		KeyGenerator kg = KeyGenerator.getInstance("AES", "SunJCE");
		kg.init(256);
		Key key = kg.generateKey();
		Cipher ciph = Cipher.getInstance("AES", "SunJCE");
		ciph.init(Cipher.ENCRYPT_MODE, key);
		byte[] ciphered = ciph.doFinal(msg);
		ciph.init(Cipher.DECRYPT_MODE, key);
		byte[] deciphered = ciph.doFinal(ciphered);
	}
}
