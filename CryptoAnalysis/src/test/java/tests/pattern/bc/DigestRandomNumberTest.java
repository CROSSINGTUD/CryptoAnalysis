package tests.pattern.bc;

import java.io.File;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.prng.DigestRandomGenerator;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class DigestRandomNumberTest extends UsagePatternTestingFramework {

	@Override
	protected String getSootClassPath() {
		// TODO Auto-generated method stub
		String sootCp = super.getSootClassPath();
		
		sootCp += File.pathSeparator + "/Users/rakshitkr/.m2/repository/org/bouncycastle/bcprov-jdk15on/1.60/bcprov-jdk15on-1.60.jar";

		return sootCp; 
	}
	
	@Test
	public void test1() {
		Digest digest = new SHA256Digest();
		DigestRandomGenerator rGen = new DigestRandomGenerator(digest);
		byte[] seed = Hex.decode("81dcfafc885914057876");
		byte[] output = new byte[digest.getDigestSize()];
		rGen.addSeedMaterial(seed);
		for (int i = 0; i != 1024; i++)
        {
             rGen.nextBytes(output);
        }
		
		Assertions.hasEnsuredPredicate(digest);
		Assertions.hasEnsuredPredicate(output);
		
		Assertions.mustBeInAcceptingState(rGen);
	}
}
