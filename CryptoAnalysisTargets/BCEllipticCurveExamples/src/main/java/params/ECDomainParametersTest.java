package params;

import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.util.encoders.Hex;

import constants.Constants;

@SuppressWarnings("unused")
public class ECDomainParametersTest {
	public void testOne(String point) {
		ECDomainParameters params = new ECDomainParameters(Constants.curve, 
				Constants.curve.decodePoint(Hex.decode(point)),
				Constants.n);
	}
	
	public void testTwo(String point) {
		ECDomainParameters params = new ECDomainParameters(Constants.curve, 
				Constants.curve.decodePoint(Hex.decode(point)),
				Constants.n,
				Constants.n);
	}
	
	public void testThree(String point) {
		ECDomainParameters params = new ECDomainParameters(Constants.curve, 
				Constants.curve.decodePoint(Hex.decode(point)),
				Constants.n,
				Constants.n,
				Hex.decode(point));
	}
}
