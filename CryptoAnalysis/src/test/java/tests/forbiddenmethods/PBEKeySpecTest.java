package tests.forbiddenmethods;

import java.security.NoSuchAlgorithmException;

import javax.crypto.spec.PBEKeySpec;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class PBEKeySpecTest  extends UsagePatternTestingFramework {

	@Override
	protected String getRulesetPath() {
		return TestConstants.JCA_RULESET_PATH;
	}

	@Test
	public void PBEKeySpecTest1() throws NoSuchAlgorithmException {
		PBEKeySpec pbe = new PBEKeySpec(new char[]{});
		Assertions.callToForbiddenMethod();
	}

	@Test
	public void PBEKeySpecTest2() throws NoSuchAlgorithmException {
		PBEKeySpec pbe = new PBEKeySpec(new char[]{}, new byte[1], 1000);
		Assertions.callToForbiddenMethod();
	}

	@Test
	public void PBEKeySpecTest3() throws NoSuchAlgorithmException {
		PBEKeySpec pbe = new PBEKeySpec(new char[]{}, new byte[1], 1000);
		Assertions.callToForbiddenMethod();
	}
	
}
