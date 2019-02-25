package iso9796_signer_verifier;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAKey;
import java.security.spec.PKCS8EncodedKeySpec;

import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.ISO9796d2Signer;

public class Main {
	
	private static String privateKeyFilename = null;
	private static byte[] message = null;

	public static byte[] sign() throws Exception {
		RSAEngine rsa = new RSAEngine();
		Digest dig = new SHA1Digest();

		RSAPrivateKey privateKey = (RSAPrivateKey) getPrivate(privateKeyFilename);
		BigInteger big = ((RSAKey) privateKey).getModulus();
		ISO9796d2Signer eng = new ISO9796d2Signer(rsa, dig, true);
		RSAKeyParameters rsaPriv = new RSAKeyParameters(true, big, privateKey.getPrivateExponent());
		eng.init(true, rsaPriv);
		eng.update(message[0]);
		eng.update(message, 1, message.length - 1);

		byte[] signature = eng.generateSignature();

		return signature;
	}
	
	private static PrivateKey getPrivate(String filename) throws Exception {

		byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());

		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
	}
}
