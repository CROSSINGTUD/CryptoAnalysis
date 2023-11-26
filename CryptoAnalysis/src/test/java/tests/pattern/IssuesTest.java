package tests.pattern;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class IssuesTest extends UsagePatternTestingFramework {

	@Override
	protected Ruleset getRuleSet() {
		return Ruleset.JavaCryptographicArchitecture;
	}
	
	@Test
	public void testIssue421() throws GeneralSecurityException {
		// Related to issue 421: https://github.com/CROSSINGTUD/CryptoAnalysis/issues/421
		X509EncodedKeySpec keySpec1 = new X509EncodedKeySpec("insecureKeyBytes".getBytes());
		X509EncodedKeySpec keySpec2 = new X509EncodedKeySpec("insecureKeyBytes".getBytes());

		Assertions.notHasEnsuredPredicate(keySpec1);
		Assertions.notHasEnsuredPredicate(keySpec2);

		KeyFactory kf = KeyFactory.getInstance("RSA");
		PublicKey pubkey1 = kf.generatePublic(keySpec1);
		Assertions.notHasEnsuredPredicate(pubkey1);
		
		PublicKey pubkey2 = kf.generatePublic(keySpec2);
		Assertions.notHasEnsuredPredicate(pubkey2);

		Assertions.predicateErrors(4);
	}

}
