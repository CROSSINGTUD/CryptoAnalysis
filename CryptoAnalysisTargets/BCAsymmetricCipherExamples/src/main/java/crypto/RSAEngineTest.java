package crypto;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.util.encoders.Hex;

import constants.Constants;

public class RSAEngineTest {

	public void testEncryptOne() throws InvalidCipherTextException {
	    String edgeInput = "ff6f77206973207468652074696d6520666f7220616c6c20676f6f64206d656e";
	    byte[] data = Hex.decode(edgeInput);
		RSAKeyParameters pubParameters = new RSAKeyParameters(false, Constants.mod, Constants.pubExp);
		AsymmetricBlockCipher eng = new RSAEngine();
        eng.init(true, pubParameters);
        byte[] cipherText = eng.processBlock(data, 0, data.length);
	}
	
	public void testEncryptTwo() throws InvalidCipherTextException {
	    String edgeInput = "ff6f77206973207468652074696d6520666f7220616c6c20676f6f64206d656e";
	    byte[] data = Hex.decode(edgeInput);
		RSAKeyParameters pubParameters = new RSAKeyParameters(false, Constants.mod, Constants.pubExp);
		AsymmetricBlockCipher eng = new RSAEngine();
        // missing init()
        byte[] cipherText = eng.processBlock(data, 0, data.length);
	}
	
	public void testDecryptOne(byte[] data) throws InvalidCipherTextException {
        RSAKeyParameters privParameters = new RSAPrivateCrtKeyParameters(Constants.mod, Constants.pubExp, Constants.privExp, Constants.p, Constants.q, Constants.pExp, Constants.qExp, Constants.crtCoef);
		AsymmetricBlockCipher eng = new RSAEngine();
        eng.init(false, privParameters);
        byte[] plainText = eng.processBlock(data, 0, data.length);
	}
	
	public void testDecryptTwo(byte[] data) throws InvalidCipherTextException {
        RSAKeyParameters privParameters = new RSAPrivateCrtKeyParameters(Constants.mod, Constants.pubExp, Constants.privExp, Constants.p, Constants.q, Constants.pExp, Constants.qExp, Constants.crtCoef);
		AsymmetricBlockCipher eng = new RSAEngine();
		// missing init()
		byte[] plainText = eng.processBlock(data, 0, data.length);
	}
}
