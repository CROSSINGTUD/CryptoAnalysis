package transforms;

import org.bouncycastle.crypto.ec.ECFixedTransform;
import org.bouncycastle.crypto.ec.ECPair;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import constants.Constants;

public class ECFixedTransformTest {
	
	public void testOne(String point) {
		ECDomainParameters params = new ECDomainParameters(Constants.curve, 
				Constants.curve.decodePoint(Hex.decode(point)), 
				Constants.n, 
				Constants.n, 
				Hex.decode(point));
		ECPoint data = Constants.priKey.getParameters().getG().multiply(Constants.n);
		ECPair cipherText = new ECPair(data, data);
		ECPublicKeyParameters pubKey = new ECPublicKeyParameters(Constants.curve.decodePoint(Hex.decode(point)), params);
		ECFixedTransform ecTrans = new ECFixedTransform(Constants.n);
		ecTrans.init(pubKey);
		ecTrans.transform(cipherText);
	}
	
	public void testTwo(String point) {
		ECDomainParameters params = new ECDomainParameters(Constants.curve, 
				Constants.curve.decodePoint(Hex.decode(point)), 
				Constants.n, 
				Constants.n, 
				Hex.decode(point));
		ECPoint data = Constants.priKey.getParameters().getG().multiply(Constants.n);
		ECPair cipherText = new ECPair(data, data);
		ECPublicKeyParameters pubKey = new ECPublicKeyParameters(Constants.curve.decodePoint(Hex.decode(point)), params);
		ECFixedTransform ecTrans = new ECFixedTransform(Constants.n);
		ecTrans.init(pubKey);
		ecTrans.transform(cipherText);
		ecTrans.transform(cipherText);
		ecTrans.transform(cipherText);
	}
	
	public void testThree(String point) {
		ECPoint data = Constants.priKey.getParameters().getG().multiply(Constants.n);
		ECPair cipherText = new ECPair(data, data);
		ECPublicKeyParameters pubKey = new ECPublicKeyParameters(Constants.curve.decodePoint(Hex.decode(point)), null);
		ECFixedTransform ecTrans = new ECFixedTransform(Constants.n);
		ecTrans.init(pubKey);
		ecTrans.transform(cipherText);
	}
	
	public void testFour(String point) {
		ECDomainParameters params = new ECDomainParameters(Constants.curve, 
				Constants.curve.decodePoint(Hex.decode(point)), 
				Constants.n, 
				Constants.n, 
				Hex.decode(point));
		ECPoint data = Constants.priKey.getParameters().getG().multiply(Constants.n);
		ECPair cipherText = new ECPair(data, data);
		ECPublicKeyParameters pubKey = new ECPublicKeyParameters(Constants.curve.decodePoint(Hex.decode(point)), params);
		ECFixedTransform ecTrans = new ECFixedTransform(null);
		ecTrans.init(pubKey);
		ecTrans.transform(cipherText);
	}
	
	/**
	 * With TypestateError */
	public void testFive(String point) {
		ECPoint data = Constants.priKey.getParameters().getG().multiply(Constants.n);
		ECPair cipherText = new ECPair(data, data);
		ECFixedTransform ecTrans = new ECFixedTransform(Constants.n);
		ecTrans.transform(cipherText);
	}
	
	/**
	 * With IncompleteOperationError */
	public void testSix(String point) {
		ECDomainParameters params = new ECDomainParameters(Constants.curve, 
				Constants.curve.decodePoint(Hex.decode(point)), 
				Constants.n, 
				Constants.n, 
				Hex.decode(point));
		ECPublicKeyParameters pubKey = new ECPublicKeyParameters(Constants.curve.decodePoint(Hex.decode(point)), params);
		ECFixedTransform ecTrans = new ECFixedTransform(Constants.n);
		ecTrans.init(pubKey);
	}
	
}
