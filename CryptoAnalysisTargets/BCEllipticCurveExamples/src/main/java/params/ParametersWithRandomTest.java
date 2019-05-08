package params;

import java.security.SecureRandom;

import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.encoders.Hex;

import constants.Constants;

@SuppressWarnings("unused")
public class ParametersWithRandomTest {
		
	public void testOne(String point) {
		ECDomainParameters params = new ECDomainParameters(Constants.curve, Constants.curve.decodePoint(Hex.decode(point)), Constants.n, Constants.n, Hex.decode(point));
		ECPublicKeyParameters pubKey = new ECPublicKeyParameters(Constants.curve.decodePoint(Hex.decode(point)), params);
		ParametersWithRandom pubKeyRand = new ParametersWithRandom(pubKey);
	}
	
	public void testTwo(String point) {
		ECDomainParameters params = new ECDomainParameters(Constants.curve, Constants.curve.decodePoint(Hex.decode(point)), Constants.n, Constants.n, new SecureRandom().generateSeed(10));
		ECPublicKeyParameters pubKey = new ECPublicKeyParameters(Constants.curve.decodePoint(Hex.decode(point)), params);
		ParametersWithRandom pubKeyRand = new ParametersWithRandom(pubKey);
	}
	
	public void testThree(String point) {
		ECDomainParameters params = new ECDomainParameters(Constants.curve, Constants.curve.decodePoint(Hex.decode(point)), Constants.n, Constants.n, Hex.decode(point));
		ECPublicKeyParameters pubKey = new ECPublicKeyParameters(Constants.curve.decodePoint(Hex.decode(point)), params);
		ParametersWithRandom pubKeyRand = new ParametersWithRandom(pubKey, null);
	}
}
