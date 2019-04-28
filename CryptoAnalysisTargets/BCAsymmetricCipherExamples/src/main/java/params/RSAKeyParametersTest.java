package params;

import org.bouncycastle.crypto.params.RSAKeyParameters;

import constants.Constants;

@SuppressWarnings("unused")
public class RSAKeyParametersTest {

	public void testOne() {
		RSAKeyParameters params = new RSAKeyParameters(
				true, 
				Constants.mod, 
				Constants.privExp);
	}

	public void testTwo() {
		RSAKeyParameters params = new RSAKeyParameters(
				false, 
				Constants.mod, 
				Constants.pubExp);
	}
}
