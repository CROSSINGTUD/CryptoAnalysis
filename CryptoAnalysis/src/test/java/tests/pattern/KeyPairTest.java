package tests.pattern;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.RSAKeyGenParameterSpec;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class KeyPairTest extends UsagePatternTestingFramework {

	@Override
	protected Ruleset getRuleSet() {
		return Ruleset.JavaCryptographicArchitecture;
	}
	
	@Test
	public void negativeRsaParameterSpecTest() throws GeneralSecurityException, IOException {
		Integer keySize = new Integer(102);
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		RSAKeyGenParameterSpec parameters = new RSAKeyGenParameterSpec(keySize, RSAKeyGenParameterSpec.F4);
		Assertions.notHasEnsuredPredicate(parameters);
		Assertions.extValue(0);
		generator.initialize(parameters, new SecureRandom());
		KeyPair keyPair = generator.generateKeyPair();
		Assertions.notHasEnsuredPredicate(keyPair);
	}

	@Test
	public void positiveRsaParameterSpecTest() throws GeneralSecurityException, IOException {
		Integer keySize = new Integer(2048);
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		RSAKeyGenParameterSpec parameters = new RSAKeyGenParameterSpec(keySize, RSAKeyGenParameterSpec.F4);
		Assertions.extValue(0);
		Assertions.extValue(1);
		Assertions.hasEnsuredPredicate(parameters);
		generator.initialize(parameters, new SecureRandom());
		KeyPair keyPair = generator.generateKeyPair();
		Assertions.hasEnsuredPredicate(keyPair);
	}

	@Test
	public void positiveRsaParameterSpecTestBigInteger() throws GeneralSecurityException, IOException {
		Integer keySize = new Integer(2048);
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		RSAKeyGenParameterSpec parameters = new RSAKeyGenParameterSpec(keySize, BigInteger.valueOf(65537));
		Assertions.extValue(0);
		Assertions.extValue(1);
		Assertions.hasEnsuredPredicate(parameters);
		generator.initialize(parameters, new SecureRandom());
		KeyPair keyPair = generator.generateKeyPair();
		Assertions.hasEnsuredPredicate(keyPair);
	}

}
