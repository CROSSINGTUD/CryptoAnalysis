package typestate.tests.crypto;

import java.io.File;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;

import test.IDEALCrossingTestingFramework;

public class CipherTest extends IDEALCrossingTestingFramework{

	@Override
	protected File getSMGFile() {
		return new File("Cipher.smg");
	}
	@Test
	public void testCipher1() throws NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher c = Cipher.getInstance("AES");
		Benchmark.assertState(c, 0);
	}

	@Test
	public void testCipher2() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher c = Cipher.getInstance("AES");
		c.init(1, new SecretKeySpec(null, "AES"));
		
		Benchmark.assertState(c, 1);
	}

	@Test
	public void testCipher3() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance("AES");
		c.init(1, new SecretKeySpec(null, "AES"));
		c.doFinal(null);
		
		Benchmark.assertState(c, 2);
	}

	@Test
	public void testCipher4() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance("AES");
		c.init(1, new SecretKeySpec(null, "AES"));
		c.doFinal(null);
		c.doFinal(null);
		c.doFinal(null);
		
		Benchmark.assertState(c, 2);
	}

	@Test
	public void testCipher5() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance("AES");
		c.init(1, new SecretKeySpec(null, "AES"));
		c.update(null);
		c.doFinal(null);
		
		Benchmark.assertState(c, 2);
	}

	@Test
	public void testCipher6() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance("AES");
		c.init(1, new SecretKeySpec(null, "AES"));
		c.update(null);
		
		Benchmark.assertState(c, 3);
	}

	@Test
	public void testCipher7() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance("AES");
		c.init(1, new SecretKeySpec(null, "AES"));
		c.update(null);
		c.doFinal(null);
		c.init(2, new SecretKeySpec(null, "AES"));
		
		Benchmark.assertState(c, -1);
	}

	@Test
	public void testCipher8() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance("AES");
		c.init(1, new SecretKeySpec(null, "AES"));
		c.doFinal(null);
		c = Cipher.getInstance("AES");
		c.init(2, new SecretKeySpec(null, "AES"));
		c.doFinal(null);
		
		Benchmark.assertState(c, 2);
	}

	@Test
	public void testCipher9() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance("AES");
		c.update(null);
		c.doFinal(null);
		
		Benchmark.assertState(c, -1);
	}

	@Test
	public void testCipher10() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance("AES");
		c.doFinal(null);
		
		Benchmark.assertState(c, -1);	
	}
}
