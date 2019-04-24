package fabric_api_archieve;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequenceGenerator;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;

public class Crypto {
	
	static final X9ECParameters curve = SECNamedCurves.getByName("secp256k1");
	static final ECDomainParameters domain = new ECDomainParameters(curve.getCurve(), curve.getG(), curve.getN(), curve.getH());
	static final BigInteger HALF_CURVE_ORDER = curve.getN().shiftRight(1);
	
	public byte[] sign(byte[] hash, byte[] privateKey) {
		ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
		signer.init(true, new ECPrivateKeyParameters(new BigInteger(privateKey), domain));
		BigInteger[] signature = signer.generateSignature(hash);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			DERSequenceGenerator seq = new DERSequenceGenerator(baos);
			seq.addObject(new ASN1Integer(signature[0]));
			seq.addObject(new ASN1Integer(toCanonicalS(signature[1])));
			seq.close();
			return baos.toByteArray();
		} catch (IOException e) {
			return new byte[0];
		}
	}
	
	private BigInteger toCanonicalS(BigInteger s) {
        if (s.compareTo(HALF_CURVE_ORDER) <= 0) {
            return s;
        } else {
            return curve.getN().subtract(s);
        }
    }
}
