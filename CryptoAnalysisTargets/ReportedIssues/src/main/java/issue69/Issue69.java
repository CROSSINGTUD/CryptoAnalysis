package issue69;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;

public class Issue69 {

	private static final String KEY_ALGORITHM = "RSA";

	public void encryptByPublicKey(String publicKey) throws Exception {
		byte[] keyBytes = new Base64().decode(publicKey.getBytes());
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key publicK = keyFactory.generatePublic(x509KeySpec);

		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());

		// RequiredPredicateError because no predicate is ensured on 'keyBytes'
		cipher.init(Cipher.ENCRYPT_MODE, publicK);
	}
}
