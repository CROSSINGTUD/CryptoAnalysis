package generators;

import java.security.SecureRandom;

import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.util.encoders.Hex;

import constants.Constants;

public class ECKeyPairGeneratorTest {
	
	/**
	 * Correct usage */
	public void testOne(String point) {
		ECDomainParameters params = new ECDomainParameters(Constants.curve, 
				Constants.curve.decodePoint(Hex.decode(point)),
				Constants.n);
		ECKeyGenerationParameters ecGenParams = new ECKeyGenerationParameters(params, new SecureRandom());
		ECKeyPairGenerator keyPairGen = new ECKeyPairGenerator();
		keyPairGen.init(ecGenParams);
		keyPairGen.generateKeyPair();
	}
	
	/**
	 * With TypestateError */
	public void testTwo(String point) {
		ECKeyPairGenerator keyPairGen = new ECKeyPairGenerator();
		keyPairGen.generateKeyPair();
	}
	
	/**
	 * With IncompleteOperationError */
	public void testThree(String point) {
		ECDomainParameters params = new ECDomainParameters(Constants.curve, 
				Constants.curve.decodePoint(Hex.decode(point)),
				Constants.n);
		ECKeyGenerationParameters ecGenParams = new ECKeyGenerationParameters(params, new SecureRandom());
		ECKeyPairGenerator keyPairGen = new ECKeyPairGenerator();
		keyPairGen.init(ecGenParams);
	}

}
