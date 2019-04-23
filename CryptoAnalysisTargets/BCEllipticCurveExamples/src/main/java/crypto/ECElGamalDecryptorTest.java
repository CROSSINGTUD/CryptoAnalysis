package crypto;

import org.bouncycastle.crypto.ec.ECElGamalDecryptor;
import org.bouncycastle.crypto.ec.ECPair;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import constants.Constants;

@SuppressWarnings("unused")
public class ECElGamalDecryptorTest {
	
	public void testOne(String point) {
        ECElGamalDecryptor decryptor = new ECElGamalDecryptor();
        decryptor.init(Constants.priKey);
        ECPair pair = new ECPair(Constants.curve.decodePoint(Hex.decode(point)), Constants.curve.decodePoint(Hex.decode(point)));
        ECPoint plainText = decryptor.decrypt(pair);
	}

	public void testTwo(String point) {
        ECElGamalDecryptor decryptor = new ECElGamalDecryptor();
        decryptor.init(null);
        ECPair pair = new ECPair(Constants.curve.decodePoint(Hex.decode(point)), Constants.curve.decodePoint(Hex.decode(point)));
        ECPoint plainText = decryptor.decrypt(pair);
	}

	public void testThree() {
        ECElGamalDecryptor decryptor = new ECElGamalDecryptor();
        decryptor.init(Constants.priKey);
        ECPair pair2 = new ECPair(null, null);
        ECPoint plainText = decryptor.decrypt(pair2);
	}

	public void testFour() {
        ECElGamalDecryptor decryptor = new ECElGamalDecryptor();
        ECPair pair2 = new ECPair(null, null);
        ECPoint plainText = decryptor.decrypt(pair2);
	}
}
