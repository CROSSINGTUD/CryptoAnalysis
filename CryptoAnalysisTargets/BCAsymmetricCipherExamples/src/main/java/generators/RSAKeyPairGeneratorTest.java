package generators;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;

import constants.Constants;

public class RSAKeyPairGeneratorTest {

	public void testOne() throws NoSuchAlgorithmException {
		RSAKeyPairGenerator generator = new RSAKeyPairGenerator();
		KeyGenerationParameters params = new RSAKeyGenerationParameters(
				Constants.publicExponent,
				SecureRandom.getInstance("SHA1PRNG"),
				Constants.strength,
				Constants.certainty
				);
		generator.init(params);;
		generator.generateKeyPair();
	}
	
	public void testTwo() {
		RSAKeyPairGenerator generator = new RSAKeyPairGenerator();
		generator.init(null);
		generator.generateKeyPair();
	}
	
	public void testThree() {
		RSAKeyPairGenerator generator = new RSAKeyPairGenerator();
		// missing init()
		generator.generateKeyPair();
	}
	
	public void testFour() throws NoSuchAlgorithmException {
		RSAKeyPairGenerator generator = new RSAKeyPairGenerator();
		KeyGenerationParameters params = new RSAKeyGenerationParameters(
				Constants.publicExponent,
				SecureRandom.getInstance("SHA1PRNG"),
				Constants.strength,
				Constants.certainty
				);
		generator.init(params);;
		// missing generateKeyPair()
	}
}
