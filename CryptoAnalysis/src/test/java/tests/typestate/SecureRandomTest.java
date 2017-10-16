package tests.typestate;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.junit.Test;

import test.IDEALCrossingTestingFramework;
import test.assertions.Assertions;

public class SecureRandomTest extends IDEALCrossingTestingFramework{

	@Override
	protected File getCryptSLFile() {
		return new File("SecureRandom.cryptslbin");
	}
	@Test
	public void testSecureRandom1() throws NoSuchAlgorithmException {
		final byte[] salt = new byte[32];
		SecureRandom sr = SecureRandom.getInstanceStrong();
		sr.nextBytes(salt);
		Assertions.assertState(sr, 2);
	}
	@Test
	public void testSecureRandom2() throws NoSuchAlgorithmException {
		final byte[] salt = new byte[32];
		SecureRandom sr = new SecureRandom();
		sr.nextBytes(salt);
		Assertions.assertState(sr, 2);
	}
	@Test
	public void testSecureRandom3() throws NoSuchAlgorithmException {
		SecureRandom sr = new SecureRandom();
		Assertions.assertState(sr, 0);
	}
}
