package password_safe;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;

public class Crypto {

	public static byte[] encryptKeyRSA(byte[] encryptionKey, String toBeEncrypted) throws Exception
	{
		PKCS1Encoding rsa = new PKCS1Encoding(new RSAEngine());
		rsa.init(true, getCipherParameters(encryptionKey));

		byte[] k = toBeEncrypted.getBytes();

		byte[] encrypted = rsa.processBlock(k, 0, k.length);
		return encrypted;
	}

	public static String decryptKeyRSA(byte[] encryptionKey, byte[] encrypted) throws Exception
	{
		PKCS1Encoding rsa = new PKCS1Encoding(new RSAEngine());
		rsa.init(false, getCipherParameters(encryptionKey));

		byte[] b = encrypted;

		byte[] decrypted = rsa.processBlock(b, 0, b.length);
		return decrypted.toString();
	}

	private static CipherParameters getCipherParameters(byte[] encryptionKey) {
		// TODO Auto-generated method stub
		return null;
	}
}
