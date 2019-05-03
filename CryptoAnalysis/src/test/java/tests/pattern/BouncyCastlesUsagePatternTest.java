package tests.pattern;

import java.io.File;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class BouncyCastlesUsagePatternTest extends UsagePatternTestingFramework {

	@Override
	protected Ruleset getRuleSet() {
		return Ruleset.BouncyCastle;
	}
	
	@Test
	public void testEncryptTwo() throws InvalidCipherTextException {
	    String edgeInput = "ff6f77206973207468652074696d6520666f7220616c6c20676f6f64206d656e";
	    byte[] data = Hex.decode(edgeInput);
		RSAKeyParameters pubParameters = new RSAKeyParameters(false, null, null);
		AsymmetricBlockCipher eng = new RSAEngine();
        // missing init()
//		eng.init(true, pubParameters);
        byte[] cipherText = eng.processBlock(data, 0, data.length);
        Assertions.mustNotBeInAcceptingState(eng);
	}
	
	@Override
	protected String getSootClassPath() {
		String bouncyCastleJarPath = new File("src/test/resources/bcprov-jdk15on-1.60.jar").getAbsolutePath();
		return super.getSootClassPath() +File.pathSeparator +bouncyCastleJarPath;
	}
	
}
