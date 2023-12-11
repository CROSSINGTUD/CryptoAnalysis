package cib.paramsPBE;

import javax.crypto.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.spec.PBEKeySpec;
import org.bouncycastle.jce.provider.*;

// 
public final class PBEwConstSalt1 {

	@SuppressWarnings("empty-statement")
	public static void main(String args[]) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			BadPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeySpecException,
			InvalidAlgorithmParameterException {

		Security.addProvider(new BouncyCastleProvider());

		if (args != null) {
			char[] password = args[0].toCharArray();

			byte[] salt;
			salt = "1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF".getBytes();

			int iterationCount = 2048;
			PBEKeySpec pbeks = new PBEKeySpec(password, salt, iterationCount);
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBEWithSHA256And128BitAES-CBC-BC", "BC");
			Key sk = skf.generateSecret(pbeks);
			Cipher c = Cipher.getInstance("AES/CTR/NoPadding", "BC");

			c.init(Cipher.ENCRYPT_MODE, sk);
			byte[] text = "Testando o AES..".getBytes();
			byte[] enrypted = c.doFinal(text);

			c.init(Cipher.DECRYPT_MODE, sk);
			byte[] decrypted = c.doFinal(enrypted);

		}
	}
}
