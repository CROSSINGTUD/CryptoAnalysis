package gwt_crypto;

import java.math.BigInteger;
import java.util.Arrays;

import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.RSABlindingEngine;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSABlindingFactorGenerator;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSABlindingParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.prng.FixedSecureRandom;
import org.bouncycastle.crypto.signers.PSSSigner;

public class PSSBlindTest {

	public void testSig(
		    int                 id,
		    RSAKeyParameters    pub,
		    RSAKeyParameters    prv,
		    byte[]              slt,
		    byte[]              msg,
		    byte[]              sig)
		    throws Exception
		{
		    RSABlindingFactorGenerator blindFactorGen = new RSABlindingFactorGenerator();
		    RSABlindingEngine blindingEngine = new RSABlindingEngine();
		    PSSSigner blindSigner = new PSSSigner(blindingEngine, new SHA1Digest(), 20);
		    PSSSigner signer = new PSSSigner(new RSAEngine(), new SHA1Digest(), 20);

		    blindFactorGen.init(pub);

		    BigInteger blindFactor = blindFactorGen.generateBlindingFactor();
		    RSABlindingParameters params = new RSABlindingParameters(pub, blindFactor);

		    // generate a blind signature
		    blindSigner.init(true, new ParametersWithRandom(params, new FixedSecureRandom(slt)));

		    blindSigner.update(msg, 0, msg.length);

		    byte[] blindedData = blindSigner.generateSignature();

		    RSAEngine signerEngine = new RSAEngine();

		    signerEngine.init(true, prv);

		    byte[] blindedSig = signerEngine.processBlock(blindedData, 0, blindedData.length);

		    // unblind the signature
		    blindingEngine.init(false, params);

		    byte[] s = blindingEngine.processBlock(blindedSig, 0, blindedSig.length);

		    //signature verification
		    if (!Arrays.equals(s, sig))
		    {
		        System.out.println("test " + id + " failed generation");
		    }
		    
		    //verify signature with PSSSigner
		    signer.init(false, pub);
		    signer.update(msg, 0, msg.length);

		    if (!signer.verifySignature(s))
		    {
		        System.out.println("test " + id + " failed PSSSigner verification");
		    }
		}
}
