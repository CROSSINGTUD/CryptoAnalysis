package java_security;

import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class PasswordBasedEncryption {
	
	private static final String constantSalt = "This is a long fixed phrase that will be used each time as the salt. Both the encryption and decryption use the same salt.";
	private static final int iterations = 10000;
	
	/**
	 * A password-based data decryption using a constant salt value "<b>constantSalt</b>"
	 * @param cipher
	 * @param password
	 * @param salt
	 * @param iterationCount
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] cipher, String password) throws Exception
	{
	    PKCS12ParametersGenerator pGen = new PKCS12ParametersGenerator(new SHA256Digest());
	    char[] passwordChars = password.toCharArray();
	    final byte[] pkcs12PasswordBytes = PBEParametersGenerator.PKCS12PasswordToBytes(passwordChars);
	    pGen.init(pkcs12PasswordBytes, constantSalt.getBytes(), iterations);
	    CBCBlockCipher aesCBC = new CBCBlockCipher(new AESEngine());
	    ParametersWithIV aesCBCParams = (ParametersWithIV) pGen.generateDerivedParameters(256, 128);
	    aesCBC.init(false, aesCBCParams);
	    PaddedBufferedBlockCipher aesCipher = new PaddedBufferedBlockCipher(aesCBC, new PKCS7Padding());
	    byte[] plainTemp = new byte[aesCipher.getOutputSize(cipher.length)];
	    int offset = aesCipher.processBytes(cipher, 0, cipher.length, plainTemp, 0);
	    int last = aesCipher.doFinal(plainTemp, offset);
	    final byte[] plain = new byte[offset + last];
	    System.arraycopy(plainTemp, 0, plain, 0, plain.length);
	    return plain;
	}
}
