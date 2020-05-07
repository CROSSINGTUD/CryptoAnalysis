

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

/**
 * This code contains a misuse example CogniCrypt_SAST of a Cipher object. 
 * CogniCrypt_SAST reports that the string argument to Cipher.getInstance("AES/ECB/PKCS5Padding") does not correspond the CrySL specification. 
 *
 */
public class MainClass {
	public static void main(String...args) throws NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher instance = Cipher.getInstance("AES/ECB/PKCS5Padding");
	}
	
	class A {
		
		A() {
			try {
				Cipher instance = Cipher.getInstance("AES/ECB/PKCS5Padding");
			} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
				e.printStackTrace();
			}
		}
	}
}

class B {
	void methodA() throws NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher instance = Cipher.getInstance("AES/ECB/PKCS5Padding");
	}	
}
