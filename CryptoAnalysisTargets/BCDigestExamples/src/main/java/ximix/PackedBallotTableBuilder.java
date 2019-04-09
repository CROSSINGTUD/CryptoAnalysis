package ximix;

import java.math.BigInteger;
import java.util.Map;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.math.ec.ECPoint;

public class PackedBallotTableBuilder {
	
	// replaced with dummy values
	private final byte[] seed = new byte[32]; 
	private final ECDomainParameters domainParameters = new ECDomainParameters(null, null, null);
	private final Map<ECPoint, byte[]> packMap = null;

	public ECPoint generatePackPoint(SHA256Digest digest, byte[] ballot, byte[] hash)
	{
	    BigInteger element;
	    ECPoint point;
	    do
	    {
	        digest.update(seed, 0, seed.length);

	        for (int b = 0; b != ballot.length; b++)
	        {
	            digest.update(ballot[b]);
	        }

	        digest.doFinal(hash, 0);

	        digest.update(hash, 0, hash.length);

	        element = new BigInteger(1, hash).mod(domainParameters.getN());
	        point = domainParameters.getG().multiply(element).normalize();
	    }
	    while (element.equals(BigInteger.ZERO) || packMap.containsKey(point));
	    return point;
	}
}
