

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public class ConstraintErrorExample {

	public static void main(String[] args) {

		try {
			Cipher instance = Cipher.getInstance("AES/ECB/PKCS5Padding");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}
}
