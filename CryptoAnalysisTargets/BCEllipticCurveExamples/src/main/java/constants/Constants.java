package constants;

import java.math.BigInteger;

import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.encoders.Hex;

public class Constants {
	
	public static BigInteger n = new BigInteger("62771017353866");
	
	public static ECCurve.Fp curve = new ECCurve.Fp(
							        new BigInteger("2343"),
							        new BigInteger("2343"),
							        new BigInteger("2343"),
							        n, ECConstants.ONE);
	
	public static ECDomainParameters params = new ECDomainParameters(
            curve,
            curve.decodePoint(Hex.decode("03188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012")), // G
            n);
	
	public static ECPublicKeyParameters pubKeyValid = new ECPublicKeyParameters(
            curve.decodePoint(Hex.decode("0262b12d")), // Q
            params);
	
	public static ECPrivateKeyParameters priKey = new ECPrivateKeyParameters(
		    new BigInteger("6510567709"), // d
		    params);
	
}
