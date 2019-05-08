package crypto;

import java.security.SecureRandom;

import org.bouncycastle.crypto.ec.ECElGamalEncryptor;
import org.bouncycastle.crypto.ec.ECPair;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import constants.Constants;


@SuppressWarnings("unused")
public class ECElGamalEncryptorTest {

	public void testOne() {
		ECPoint data = Constants.pubKeyValid.getParameters().getG().multiply(Constants.n);
		ECElGamalEncryptor encryptor = new ECElGamalEncryptor();
		encryptor.init(Constants.pubKeyValid);
        ECPair cipherText = encryptor.encrypt(data);
	}

	public void testTwo() {
		ECPoint data = Constants.pubKeyValid.getParameters().getG().multiply(Constants.n);
		ECElGamalEncryptor encryptor = new ECElGamalEncryptor();
		ECPair cipherText = encryptor.encrypt(data);
	}
	
	public void testThree(String point) {
		ECDomainParameters params = new ECDomainParameters(Constants.curve, Constants.curve.decodePoint(Hex.decode(point)), Constants.n);
		ECPublicKeyParameters pubKey = new ECPublicKeyParameters(Constants.curve.decodePoint(Hex.decode(point)), params);
		ECPoint data = pubKey.getParameters().getG().multiply(Constants.n);
		ECElGamalEncryptor encryptor = new ECElGamalEncryptor();
		encryptor.init(pubKey);
		ECPair cipherText = encryptor.encrypt(data);
	}
	
	public void testFour(String point) {
		ECDomainParameters params = new ECDomainParameters(Constants.curve, Constants.curve.decodePoint(Hex.decode(point)), Constants.n);
		ECPublicKeyParameters pubKey = new ECPublicKeyParameters(Constants.curve.decodePoint(Hex.decode(point)), params);
		ParametersWithRandom parRand = new ParametersWithRandom(pubKey, new SecureRandom());
		ECPoint data = pubKey.getParameters().getG().multiply(Constants.n);
		ECElGamalEncryptor encryptor = new ECElGamalEncryptor();
		encryptor.init(parRand);
		ECPair cipherText = encryptor.encrypt(data);
	}
}
