package typestate.tests.crypto;

import java.io.File;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;

import org.junit.Test;

import test.IDEALCrossingTestingFramework;

public class KeyGeneratorTest extends IDEALCrossingTestingFramework {

	@Override
	protected File getSMGFile() {
		return new File("KeyGenerator.smg");
	}

	@Test
	public void testKeyGenerator1() throws NoSuchAlgorithmException {
		KeyGenerator c = KeyGenerator.getInstance("AES");

		Benchmark.assertState(c, 0);
	}

	@Test
	public void testKeyGenerator2() throws NoSuchAlgorithmException {
		KeyGenerator c = KeyGenerator.getInstance("AES");
		c.init(128);

		Benchmark.assertState(c, 1);
	}

	@Test
	public void testKeyGenerator3() throws NoSuchAlgorithmException {
		KeyGenerator c = KeyGenerator.getInstance("AES");
		c.init(128);
		c.generateKey();

		Benchmark.assertState(c, 2);
	}

	@Test
	public void testKeyGenerator4() throws NoSuchAlgorithmException {
		KeyGenerator c = KeyGenerator.getInstance("AES");
		c.generateKey();

		Benchmark.assertState(c, 2);
	}
}
