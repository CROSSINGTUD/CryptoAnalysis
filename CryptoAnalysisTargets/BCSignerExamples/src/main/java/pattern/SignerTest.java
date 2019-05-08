package pattern;

import java.math.BigInteger;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.signers.RSADigestSigner;
import org.bouncycastle.util.encoders.Base64;

public class SignerTest {

	public void testSignerGenerate() throws Exception {
		BigInteger dummy = new BigInteger(Base64.decode("ABCD"));

		RSAPrivateCrtKeyParameters rsaPrivate = new RSAPrivateCrtKeyParameters(dummy, dummy, dummy, dummy, dummy, dummy,
				dummy, dummy);
		Digest digest = new SHA256Digest();
		byte[] msg = new byte[] { 1, 6, 3, 32, 7, 43, 2, 5, 7, 78, 4, 23 };

		RSADigestSigner signer = new RSADigestSigner(digest);
		signer.init(true, rsaPrivate);
		signer.update(msg, 0, msg.length);
		byte[] sign = signer.generateSignature();
	}

	public void testSignerVerify() throws Exception {
		BigInteger dummy = new BigInteger(Base64.decode("ABCD"));
		RSAKeyParameters rsaPublic = new RSAKeyParameters(false, dummy, dummy);
		RSAPrivateCrtKeyParameters rsaPrivate = new RSAPrivateCrtKeyParameters(dummy, dummy, dummy, dummy, dummy, dummy,
				dummy, dummy);
		SHA256Digest digest = new SHA256Digest();
		byte[] msg = new byte[] { 1, 6, 3, 32, 7, 43, 2, 5, 7, 78, 4, 23 };
		ASN1ObjectIdentifier digOid = NISTObjectIdentifiers.id_sha256;

		RSADigestSigner signer = new RSADigestSigner(digest);
		signer.init(true, rsaPrivate);
		signer.update(msg, 0, msg.length);
		byte[] sign = signer.generateSignature();

		signer = new RSADigestSigner(digest, digOid);
		signer.init(false, rsaPublic);
		signer.update(msg, 0, msg.length);
		boolean result = signer.verifySignature(sign);
	}
}
