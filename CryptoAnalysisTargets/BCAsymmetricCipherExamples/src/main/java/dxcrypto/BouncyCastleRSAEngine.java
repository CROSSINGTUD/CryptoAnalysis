package dxcrypto;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.encodings.OAEPEncoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;

public class BouncyCastleRSAEngine {
	
	private final RSAKeyParameters publicKey;
    private final RSAKeyParameters privateKey;

    public BouncyCastleRSAEngine(RSAKeyParameters publicKey, RSAKeyParameters privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }


	public byte[] doOperation(byte[] input, boolean isEncrypt) throws Exception {
	    AsymmetricBlockCipher cipher = new OAEPEncoding(new RSAEngine(), new SHA256Digest(), new SHA1Digest(), null);
	    RSAKeyParameters key = isEncrypt ? publicKey : privateKey;
	    cipher.init(isEncrypt, key);
	    try {
	        return cipher.processBlock(input, 0, input.length);
	    } catch (InvalidCipherTextException e) {
	        throw new Exception("Encryption fails", e);
	    }
	}
}
