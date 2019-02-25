package gwt_crypto;

import java.security.SecureRandom;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.OAEPEncoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;

public class OAEPTest {

	public void encDec(
		    String label,
		    RSAKeyParameters pubParameters,
		    RSAKeyParameters privParameters,
		    byte[] seed,
		    byte[] input,
		    byte[] output)
		    throws InvalidCipherTextException
		{
		    AsymmetricBlockCipher cipher = new OAEPEncoding(new RSAEngine());

		    cipher.init(true, new ParametersWithRandom(pubParameters, new SecureRandom(seed)));

		    byte[]  out;

		    out = cipher.processBlock(input, 0, input.length);

		    for (int i = 0; i != output.length; i++)
		    {
		        if (out[i] != output[i])
		        {
		            System.out.println(label + " failed encryption");
		        }
		    }

		    cipher.init(false, privParameters);

		    out = cipher.processBlock(output, 0, output.length);

		    for (int i = 0; i != input.length; i++)
		    {
		        if (out[i] != input[i])
		        {
		        	System.out.println(label + " failed decoding");
		        }
		    }
		}
}
