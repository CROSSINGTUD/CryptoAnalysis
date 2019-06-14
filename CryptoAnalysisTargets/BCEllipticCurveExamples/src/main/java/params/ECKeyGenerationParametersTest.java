package params;

import java.security.SecureRandom;

import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.util.encoders.Hex;

import constants.Constants;

public class ECKeyGenerationParametersTest {
	
	/**
	 * Correct usage */
	public void testOne(String point) {
		ECDomainParameters params = new ECDomainParameters(Constants.curve, 
				Constants.curve.decodePoint(Hex.decode(point)),
				Constants.n);
		@SuppressWarnings("unused")
		ECKeyGenerationParameters ecGenParams = new ECKeyGenerationParameters(params, new SecureRandom());
	}
	
	/**
	 * With incorrect random */
	public void testTwo(String point) {
		ECDomainParameters params = new ECDomainParameters(Constants.curve, 
				Constants.curve.decodePoint(Hex.decode(point)),
				Constants.n);
		@SuppressWarnings("unused")
		ECKeyGenerationParameters ecGenParams = new ECKeyGenerationParameters(params, null);
	}
	
	/**
	 * With incorrect ECDomainParameters */
	public void testThree(String point) {
		@SuppressWarnings("unused")
		ECKeyGenerationParameters ecGenParams = new ECKeyGenerationParameters(null, new SecureRandom());
	}
}
