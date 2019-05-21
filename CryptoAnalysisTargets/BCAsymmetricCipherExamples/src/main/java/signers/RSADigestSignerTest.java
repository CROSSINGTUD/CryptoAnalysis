package signers;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.signers.RSADigestSigner;

import constants.Constants;

public class RSADigestSignerTest {

public void testGenerate() {
		SHA256Digest digest = new SHA256Digest();
		RSAKeyParameters privParameters = new RSAPrivateCrtKeyParameters(
				Constants.mod, 
				Constants.pubExp, 
				Constants.privExp, 
				Constants.p, 
				Constants.q, 
				Constants.pExp, 
				Constants.qExp, 
				Constants.crtCoef);
		byte[] msg = new byte[] { 1, 6, 3, 32, 7, 43, 2, 5, 7, 78, 4, 23 };
		
		RSADigestSigner signer = new RSADigestSigner(digest);
		signer.init(true, privParameters);
		signer.update(msg, 0, msg.length);
		try {
			byte[] sig = signer.generateSignature();
		} catch (DataLengthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CryptoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
