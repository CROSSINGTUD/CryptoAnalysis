package tests.typestate;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import test.IDEALCrossingTestingFramework;
import test.assertions.Assertions;

public class KeyGeneratorTest extends IDEALCrossingTestingFramework {


	@Override
	protected Ruleset getRuleset() {
		return Ruleset.JavaCryptographicArchitecture;
	}
	
	@Override
	protected String getRulename() {
		return "KeyGenerator";
	}
	

	@Test
	public void testKeyGenerator1() throws NoSuchAlgorithmException {
		KeyGenerator c = KeyGenerator.getInstance("AES");

		Assertions.assertState(c, 0);
	}

	@Test
	public void testKeyGenerator2() throws NoSuchAlgorithmException {
		KeyGenerator c = KeyGenerator.getInstance("AES");
		c.init(128);

		Assertions.assertState(c, 1);
	}

	@Test
	public void testKeyGenerator3() throws NoSuchAlgorithmException {
		KeyGenerator c = KeyGenerator.getInstance("AES");
		c.init(128);
		c.generateKey();

		Assertions.assertState(c, 2);
	}


	@Test
	public void testKeyGenerator3a() throws NoSuchAlgorithmException {
		KeyGenerator c = KeyGenerator.getInstance("AES");
		c.generateKey();
		c.init(128);
		Assertions.assertState(c, -1);
	}
	@Test
	public void testKeyGenerator4() throws NoSuchAlgorithmException {
		KeyGenerator c = KeyGenerator.getInstance("AES");
		c.generateKey();
		Assertions.assertState(c, 2);
	}
}
