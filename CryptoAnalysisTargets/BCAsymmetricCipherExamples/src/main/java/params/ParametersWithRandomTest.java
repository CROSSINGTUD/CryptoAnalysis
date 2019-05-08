package params;

import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;

import constants.Constants;

@SuppressWarnings("unused")
public class ParametersWithRandomTest {

	public void testOne() {
		
		RSAKeyParameters pubKey = new RSAKeyParameters(false, Constants.mod, Constants.pubExp);
		ParametersWithRandom pubKeyRand = new ParametersWithRandom(pubKey);
	}
	
	public void testTwo() {
		RSAKeyParameters pubKey = new RSAKeyParameters(false, Constants.mod, Constants.pubExp);
		ParametersWithRandom pubKeyRand = new ParametersWithRandom(pubKey, null);
	}
}
