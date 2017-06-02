package tests.typestate;

import java.io.File;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;

import org.junit.Test;

import test.IDEALCrossingTestingFramework;
import test.assertions.Assertions;

public class KeyGeneratorTest extends IDEALCrossingTestingFramework {

	@Override
	protected File getCryptSLFile() {
		return new File("KeyGenerator.cryptslbin");
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
	public void testKeyGenerator4() throws NoSuchAlgorithmException {
		KeyGenerator c = KeyGenerator.getInstance("AES");
		c.generateKey();
		//TODO fails because init is missing. Shouldn't we test for -1? I.e. KeyGenerator is in an error state.
		Assertions.assertState(c, 2);
	}
}
