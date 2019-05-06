package rsa_misuse;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class RSATest {
	
	public static String getHexString(byte[] b) throws Exception {
        String result = "";
        for (int i=0; i < b.length; i++) {
            result +=
                Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }
	
	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	    	data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	} 

	public static AsymmetricCipherKeyPair GenerateKeys() throws NoSuchAlgorithmException{
		
		RSAKeyPairGenerator generator = new RSAKeyPairGenerator();
		generator.init(new RSAKeyGenerationParameters
				(
						new BigInteger("10001", 16),//publicExponent
						SecureRandom.getInstance("SHA1PRNG"),//pseudorandom number generator
						4096,//strength
						80//certainty
						));

		return generator.generateKeyPair();
	}

	public static String Encrypt(byte[] data, AsymmetricKeyParameter publicKey) throws Exception{
		
		RSAEngine engine = new RSAEngine();
//		engine.init(true, publicKey); // init() is skipped

		byte[] hexEncodedCipher = engine.processBlock(data, 0, data.length);

		return getHexString(hexEncodedCipher);
	}

	public static String Decrypt(String encrypted, AsymmetricKeyParameter privateKey) throws InvalidCipherTextException{

		AsymmetricBlockCipher engine = new RSAEngine();
		engine.init(false, privateKey);

		byte[] encryptedBytes = hexStringToByteArray(encrypted);
		byte[] hexEncodedCipher = engine.processBlock(encryptedBytes, 0, encryptedBytes.length);

		return new String (hexEncodedCipher);
	}

	public static void main(String[] args) throws Exception {

		AsymmetricCipherKeyPair keyPair = GenerateKeys();

		String plainMessage = "plaintext";

		//		Encryption
		String encryptedMessage = Encrypt(plainMessage.getBytes("UTF-8"), keyPair.getPublic());

		//		Decryption
		String decryptedMessage = Decrypt(encryptedMessage, keyPair.getPrivate());

	}
}
