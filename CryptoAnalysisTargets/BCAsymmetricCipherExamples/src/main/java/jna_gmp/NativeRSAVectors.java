package jna_gmp;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;

public class NativeRSAVectors {

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	private static final BigInteger SIGNING_EXPONENT = BigInteger.valueOf(3);

	public static void generateTestVector(int rsaKeyBits, int suffix) throws Exception {
		AsymmetricCipherKeyPair pair = generateKeyPair(rsaKeyBits);

		byte[] message = new byte[rsaKeyBits / 8];
		// Clear the top bit to ensure it fits.
		message[0] &= 0x7F;

		RSAEngine encoder = new RSAEngine();
		encoder.init(true, pair.getPrivate());
		byte[] signed = encoder.processBlock(message, 0, message.length);

		RSAEngine decoder = new RSAEngine();
		decoder.init(false, pair.getPublic());
		byte[] decoded = decoder.processBlock(signed, 0, message.length);
	}

	private static AsymmetricCipherKeyPair generateKeyPair(int rsaKeyBits) throws Exception {
		RSAKeyPairGenerator generator = new RSAKeyPairGenerator();
		generator.init(new RSAKeyGenerationParameters(SIGNING_EXPONENT, SECURE_RANDOM, rsaKeyBits, 12));
		return generator.generateKeyPair();
	}

}
