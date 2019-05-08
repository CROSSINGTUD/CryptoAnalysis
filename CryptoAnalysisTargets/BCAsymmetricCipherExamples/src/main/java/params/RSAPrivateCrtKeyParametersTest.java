package params;

import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;

import constants.Constants;

//@SuppressWarnings("unused")
public class RSAPrivateCrtKeyParametersTest {

	public void testOne() {
		RSAKeyParameters privParameters = new RSAPrivateCrtKeyParameters(
				Constants.mod, 
				Constants.pubExp, 
				Constants.privExp, 
				Constants.p, 
				Constants.q, 
				Constants.pExp, 
				Constants.qExp, 
				Constants.crtCoef);
	}
}
