package params;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.util.encoders.Hex;

import constants.Constants;

@SuppressWarnings("unused")
public class ECPrivateKeyParametersTest {
	
	public void testOne(String point) {
		ECDomainParameters params = new ECDomainParameters(Constants.curve, 
				Constants.curve.decodePoint(Hex.decode(point)), 
				Constants.n, 
				Constants.n, 
				Hex.decode(point));
		ECPrivateKeyParameters priKey = new ECPrivateKeyParameters(
			    new BigInteger(point),
			    params);
	}
	
	public void testTwo(String point) {
		ECDomainParameters params = new ECDomainParameters(Constants.curve, 
				Constants.curve.decodePoint(Hex.decode(point)), 
				Constants.n, 
				Constants.n, 
				new SecureRandom().generateSeed(10));
		ECPrivateKeyParameters priKey = new ECPrivateKeyParameters(new BigInteger(point), params);
	}
}
