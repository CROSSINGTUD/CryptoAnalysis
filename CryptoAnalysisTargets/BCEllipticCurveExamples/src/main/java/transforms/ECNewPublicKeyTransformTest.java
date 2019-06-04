package transforms;

import org.bouncycastle.crypto.ec.ECNewPublicKeyTransform;
import org.bouncycastle.crypto.ec.ECPair;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import constants.Constants;

public class ECNewPublicKeyTransformTest {
	/**
	 * With ECPublicKeyParameters in init*/
	public void testOne(String point) {
		ECDomainParameters params = new ECDomainParameters(Constants.curve, 
				Constants.curve.decodePoint(Hex.decode(point)), 
				Constants.n, 
				Constants.n, 
				Hex.decode(point));
		ECPublicKeyParameters pubKey = new ECPublicKeyParameters(Constants.curve.decodePoint(Hex.decode(point)), params);
		ECPoint data = Constants.priKey.getParameters().getG().multiply(Constants.n);
		ECPair cipherText = new ECPair(data, data);
		ECNewPublicKeyTransform ecr = new ECNewPublicKeyTransform();
		ecr.init(pubKey);
		ecr.transform(cipherText);
	}
	
	/**
	 * With ParametersWithRandom in init*/
	public void testTwo(String point) {
		ECDomainParameters params = new ECDomainParameters(Constants.curve, Constants.curve.decodePoint(Hex.decode(point)), Constants.n, Constants.n, Hex.decode(point));
		ECPublicKeyParameters pubKey = new ECPublicKeyParameters(Constants.curve.decodePoint(Hex.decode(point)), params);
		ParametersWithRandom pubKeyRand = new ParametersWithRandom(pubKey);
		ECPoint data = Constants.priKey.getParameters().getG().multiply(Constants.n);
		ECPair cipherText = new ECPair(data, data);
		ECNewPublicKeyTransform ecr = new ECNewPublicKeyTransform();
		ecr.init(pubKeyRand);
		ecr.transform(cipherText);
		ecr.transform(cipherText);
		ecr.transform(cipherText);
	}
	
	/**
	 * With TypestateError*/
	public void testThree(String point) {
		ECPoint data = Constants.priKey.getParameters().getG().multiply(Constants.n);
		ECPair cipherText = new ECPair(data, data);
		ECNewPublicKeyTransform ecr = new ECNewPublicKeyTransform();
		ecr.transform(cipherText);
	}
	
	/**
	 * With IncompleteOperationError in init*/
	public void testFour(String point) {
		ECDomainParameters params = new ECDomainParameters(Constants.curve, Constants.curve.decodePoint(Hex.decode(point)), Constants.n, Constants.n, Hex.decode(point));
		ECPublicKeyParameters pubKey = new ECPublicKeyParameters(Constants.curve.decodePoint(Hex.decode(point)), params);
		ParametersWithRandom pubKeyRand = new ParametersWithRandom(pubKey);
		ECNewPublicKeyTransform ecr = new ECNewPublicKeyTransform();
		ecr.init(pubKeyRand);
	}
	
	
	public void testFive(String point) {
		ECDomainParameters params = new ECDomainParameters(Constants.curve, Constants.curve.decodePoint(Hex.decode(point)), Constants.n, Constants.n, Hex.decode(point));
		ECPublicKeyParameters pubKey = new ECPublicKeyParameters(Constants.curve.decodePoint(Hex.decode(point)), params);
		ParametersWithRandom pubKeyRand = new ParametersWithRandom(pubKey);
		ECPair cipherText = new ECPair(null, null);
		ECNewPublicKeyTransform ecr = new ECNewPublicKeyTransform();
		ecr.init(pubKeyRand);
		ecr.transform(cipherText);
	}
	
	public void testSix(String point) {
		ParametersWithRandom pubKeyRand = new ParametersWithRandom(null);
		ECPoint data = Constants.priKey.getParameters().getG().multiply(Constants.n);
		ECPair cipherText = new ECPair(data, data);
		ECNewPublicKeyTransform ecr = new ECNewPublicKeyTransform();
		ecr.init(pubKeyRand);
		ecr.transform(cipherText);
	}
}
