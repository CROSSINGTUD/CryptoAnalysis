package gwt_crypto;

import java.util.Arrays;

import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.PSSSigner;
import org.bouncycastle.util.test.FixedSecureRandom;

public class PSSTest {

	public void testSig(
		    int                 id,
		    RSAKeyParameters    pub,
		    RSAKeyParameters    prv,
		    byte[]              slt,
		    byte[]              msg,
		    byte[]              sig)
		    throws Exception
		{
		    PSSSigner           eng = new PSSSigner(new RSAEngine(), new SHA1Digest(), 20);

		    eng.init(true, new ParametersWithRandom(prv, new FixedSecureRandom(slt)));

		    eng.update(msg, 0, msg.length);

		    byte[]  s = eng.generateSignature();

		    if (!Arrays.equals(s, sig))
		    {
		        System.out.println("test " + id + " failed generation");
		    }

		    eng.init(false, pub);

		    eng.update(msg, 0, msg.length);

		    if (!eng.verifySignature(s))
		    {
		    	System.out.println("test " + id + " failed verification");
		    }
		}
}
