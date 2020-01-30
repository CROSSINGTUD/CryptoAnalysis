package pdf.insecureDefault;

import javax.crypto.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.spec.PBEKeySpec;
import org.bouncycastle.jce.provider.*;

// 
public final class InsecureDefaultPBE {

	@SuppressWarnings("empty-statement")
	public static void main(String args[]) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			BadPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeySpecException,
			InvalidAlgorithmParameterException {

		Security.addProvider(new BouncyCastleProvider());

		if (args != null) {
			char[] password = args[0].toCharArray();

			byte[] salt = new byte[16];
			(new SecureRandom()).nextBytes(salt);

			int iterationCount = 2048;
			PBEKeySpec pbeks = new PBEKeySpec(password, salt, iterationCount);
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBE", "SunJCE");
			Key sk = skf.generateSecret(pbeks);

		}
	}
}
