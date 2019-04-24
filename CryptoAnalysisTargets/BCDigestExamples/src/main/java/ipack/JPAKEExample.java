package ipack;

import java.math.BigInteger;

import org.bouncycastle.crypto.digests.SHA256Digest;

public class JPAKEExample {
	
	private static BigInteger deriveSessionKey(BigInteger keyingMaterial)
	{
		/*
		 * You should use a secure key derivation function (KDF) to derive the session key.
		 * 
		 * For the purposes of this example, I'm just going to use a hash of the keying material.
		 */
		SHA256Digest digest = new SHA256Digest();

		byte[] keyByteArray = keyingMaterial.toByteArray();

		byte[] output = new byte[digest.getDigestSize()];

		digest.update(keyByteArray, 0, keyByteArray.length);

		digest.doFinal(output, 0);

		return new BigInteger(output);
	}
}
