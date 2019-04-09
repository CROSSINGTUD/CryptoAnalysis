package bop_bitcoin_client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

import javax.xml.bind.ValidationException;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequenceGenerator;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;

public class ECKeyPair {
	
	private static final X9ECParameters curve = SECNamedCurves.getByName ("secp256k1");
	private static final ECDomainParameters domain = new ECDomainParameters (curve.getCurve (), curve.getG (), curve.getN (), curve.getH ());
	private static final SecureRandom secureRandom = new SecureRandom ();
	private BigInteger priv;
	@SuppressWarnings("unused")
	private byte[] pub;
	public byte[] sign (byte[] hash) throws ValidationException
	{
		if ( priv == null )
		{
			throw new ValidationException ("Need private key to sign");
		}
		ECDSASigner signer = new ECDSASigner (new HMacDSAKCalculator (new SHA256Digest ()));
		signer.init (true, new ECPrivateKeyParameters (priv, domain));
		BigInteger[] signature = signer.generateSignature (hash);
		ByteArrayOutputStream s = new ByteArrayOutputStream ();
		try
		{
			DERSequenceGenerator seq = new DERSequenceGenerator (s);
			seq.addObject (new ASN1Integer (signature[0]));
			seq.addObject (new ASN1Integer (signature[1]));
			seq.close ();
			return s.toByteArray ();
		}
		catch ( IOException e )
		{
		}
		return null;
	}
	
	public static ECKeyPair createNew (boolean compressed)
	{
		ECKeyPairGenerator generator = new ECKeyPairGenerator ();
		ECKeyGenerationParameters keygenParams = new ECKeyGenerationParameters (domain, secureRandom);
		generator.init (keygenParams);
		AsymmetricCipherKeyPair keypair = generator.generateKeyPair ();
		ECPrivateKeyParameters privParams = (ECPrivateKeyParameters) keypair.getPrivate ();
		ECPublicKeyParameters pubParams = (ECPublicKeyParameters) keypair.getPublic ();
		ECKeyPair k = new ECKeyPair ();
		k.priv = privParams.getD ();
		k.pub = pubParams.getQ ().getEncoded (compressed);
		return k;
	}
}
