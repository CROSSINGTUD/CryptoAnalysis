package example;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;


/**
 * This code contains a misuse example CogniCrypt_SAST of a Cipher object.
 * CogniCrypt_SAST reports that the string argument to Cipher.getInstance("PBEWithMD5AndDES") does not correspond the CrySL specification.
 *
 */
public class CipherUsesPBEWithMD5AndDESExample {

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher instance = Cipher.getInstance("PBEWithMD5AndDES");
	}

}
