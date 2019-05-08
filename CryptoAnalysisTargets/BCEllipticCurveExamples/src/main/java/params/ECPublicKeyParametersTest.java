package params;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.encoders.Hex;

import constants.Constants;

@SuppressWarnings("unused")
public class ECPublicKeyParametersTest {
		
	public void testOne(String point) {
		ECDomainParameters params = new ECDomainParameters(Constants.curve, 
				Constants.curve.decodePoint(Hex.decode(point)), 
				Constants.n, 
				Constants.n, 
				Hex.decode(point));
		ECPublicKeyParameters pubKey = new ECPublicKeyParameters(Constants.curve.decodePoint(Hex.decode(point)), params);
	}
	
	public void testTwo(String point) {
		ECDomainParameters params = new ECDomainParameters(Constants.curve, 
				Constants.curve.decodePoint(Hex.decode(point)), 
				Constants.n, 
				Constants.n, 
				new SecureRandom().generateSeed(10));
		ECPublicKeyParameters pubKey = new ECPublicKeyParameters(Constants.curve.decodePoint(Hex.decode(point)), params);
	}
}
