package pkm.constPwd4PBE;

import javax.crypto.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.spec.PBEKeySpec;
import org.bouncycastle.jce.provider.*;

public final class ConstPassword4PBE2 {

	public static void main(String args[]) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			BadPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeySpecException,
			InvalidAlgorithmParameterException {

		Security.addProvider(new BouncyCastleProvider());

		char[] password = new char[] { 'p', 'a', 's', 's', 'w', 'o', 'r', 'd' };
		byte[] salt = new byte[16];
		(new SecureRandom()).nextBytes(salt);
		int iterationCount = 2048;
		PBEKeySpec pbeks = new PBEKeySpec(password, salt, iterationCount);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBEWithSHA1And128BitAES-CBC-BC", "BC");
		Key sk = skf.generateSecret(pbeks);
		Cipher c = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");

		c.init(Cipher.ENCRYPT_MODE, sk);
		byte[] text1 = "demo text".getBytes();
		byte[] ciphered = c.doFinal(text1);

		c.init(Cipher.DECRYPT_MODE, sk);
		byte[] text2 = c.doFinal(ciphered);
	}
}
