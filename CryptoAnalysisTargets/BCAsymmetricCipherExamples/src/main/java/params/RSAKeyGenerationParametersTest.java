package params;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;

import constants.Constants;

@SuppressWarnings("unused")
public class RSAKeyGenerationParametersTest {

	public void testOne() throws NoSuchAlgorithmException {
		RSAKeyGenerationParameters params = new RSAKeyGenerationParameters(
				Constants.publicExponent, 
				SecureRandom.getInstance("SHA1PRNG"), 
				Constants.strength, 
				Constants.certainty);
	}
}


