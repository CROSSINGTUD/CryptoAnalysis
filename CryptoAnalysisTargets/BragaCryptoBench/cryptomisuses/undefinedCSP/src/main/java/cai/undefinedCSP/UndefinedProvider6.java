package cai.undefinedCSP;

import javax.crypto.*;
import java.security.*;

public final class UndefinedProvider6 {

	public static void main(String args[]) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
		byte[] msg = "demo msg".getBytes();
		KeyGenerator kg = KeyGenerator.getInstance("AES");
		kg.init(256);
		Key key = kg.generateKey();
		Cipher ciph = Cipher.getInstance("AES");
		ciph.init(Cipher.ENCRYPT_MODE, key);
		byte[] encrypted = ciph.doFinal(msg);
		ciph.init(Cipher.DECRYPT_MODE, key);
		byte[] decrypted = ciph.doFinal(encrypted);
	}
}
