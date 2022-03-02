package tests.pattern.specialrules;

import java.security.SecureRandom;
import org.junit.Test;


import crypto.analysis.CrySLRulesetSelector.Ruleset;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class SpecialRulesTest extends UsagePatternTestingFramework {
	
	@Override
	protected Ruleset getRuleSet() {
		return Ruleset.SpecialRulesForTests;
	}
	
	@Test
	public void alternativeNegatedPreds() {
		SecureRandom sr = new SecureRandom();
		byte[] genSeed = new byte[32];
		sr.nextBytes(genSeed); // this ensured genSeed as test
		Assertions.hasEnsuredPredicate(genSeed);
		SecureRandom sr2 = new SecureRandom();
		sr2.nextBytes(genSeed); 
		// genSeed should be either !test or !test2
		// since test2 was nowhere ensured, the result should throw no errors
		Assertions.errorCount(0); // this fails
	}
	
}
