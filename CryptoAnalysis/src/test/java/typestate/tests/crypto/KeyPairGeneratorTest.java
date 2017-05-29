package typestate.tests.crypto;

import java.io.File;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

import test.IDEALCrossingTestingFramework;

public class KeyPairGeneratorTest extends IDEALCrossingTestingFramework {

	@Override
	protected File getCryptSLFile() {
		return new File("KeyPairGenerator.cryptslbin");
	}

	@Test
	public void testKeyPairGenerator1() throws NoSuchAlgorithmException {
		KeyPairGenerator c = KeyPairGenerator.getInstance("RSA");
		Benchmark.assertState(c, 0);
	}
	@Test
	public void testKeyPairGenerator2() throws NoSuchAlgorithmException {
		KeyPairGenerator c = KeyPairGenerator.getInstance("RSA");
		c.initialize(128);
		Benchmark.assertState(c, 1);
	}
	@Test
	public void testKeyPairGenerator3() throws NoSuchAlgorithmException {
		KeyPairGenerator c = KeyPairGenerator.getInstance("RSA");
		c.initialize(128);
		c.generateKeyPair();
		Benchmark.assertState(c, 2);
		//TODO fails because we match java.security.KeyPairGeneratorSpi.generateKeyPair() but not java.security.KeyPairGenerator.generateKeyPair();
	}
	@Test
	public void testKeyPairGenerator4() throws NoSuchAlgorithmException {
		KeyPairGenerator c = KeyPairGenerator.getInstance("RSA");
		c.initialize(128);
		c.genKeyPair();
		Benchmark.assertState(c, 2);
		/*TODO fails because we do not match method genKeyPair, but generateKeyPair. ()
		 * genKeyPair calls generateKeyPair. But we do not analyze genKeyPair, as we excluded it.
		 */
	}
	@Test
	public void testKeyPairGenerator5() throws NoSuchAlgorithmException {
		KeyPairGenerator c = KeyPairGenerator.getInstance("RSA");
		c.generateKeyPair();
		Benchmark.assertState(c, -1);		
		//TODO fails because we match java.security.KeyPairGeneratorSpi.generateKeyPair() but not java.security.KeyPairGenerator.generateKeyPair();
	}
}
