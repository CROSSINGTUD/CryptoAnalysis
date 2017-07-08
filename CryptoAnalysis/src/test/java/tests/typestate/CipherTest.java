package tests.typestate;

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
import test.assertions.Assertions;

public class CipherTest extends IDEALCrossingTestingFramework{

	@Override
	protected File getCryptSLFile() {
		return new File("Cipher.cryptslbin");
	}
	@Test
	public void testCipher1() throws NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher c = Cipher.getInstance("AES");
		Assertions.assertState(c, 0);
	}

	@Test
	public void testCipher2() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher c = Cipher.getInstance("AES");
		c.init(1, new SecretKeySpec(null, "AES"));
		
		Assertions.assertState(c, 1);
	}

	@Test
	public void testCipher3() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance("AES");
		c.init(1, new SecretKeySpec(null, "AES"));
		c.doFinal(null);
		
		Assertions.assertState(c, 2);
	}

	@Test
	public void testCipher4() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance("AES");
		c.init(1, new SecretKeySpec(null, "AES"));
		c.doFinal(null);
		c.doFinal(null);
		c.doFinal(null);
		
		Assertions.assertState(c, 2);
	}

	@Test
	public void testCipher5() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance("AES");
		c.init(1, new SecretKeySpec(null, "AES"));
		c.update(null);
		c.doFinal(null);
		
		Assertions.assertState(c, 2);
	}

	@Test
	public void testCipher6() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance("AES");
		c.init(1, new SecretKeySpec(null, "AES"));
		c.update(null);
		
		Assertions.assertState(c, 3);
	}

	@Test
	public void testCipher7() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance("AES");
		c.init(1, new SecretKeySpec(null, "AES"));
		c.update(null);
		c.doFinal(null);
		c.init(2, new SecretKeySpec(null, "AES"));
		
		Assertions.assertState(c, -1);
	}

	@Test
	public void testCipher8() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance("AES");
		c.init(1, new SecretKeySpec(null, "AES"));
		c.doFinal(null);
		c = Cipher.getInstance("AES");
		c.init(2, new SecretKeySpec(null, "AES"));
		c.doFinal(null);
		
		Assertions.assertState(c, 2);
	}

	@Test
	public void testCipher9() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance("AES");
		c.update(null);
		Assertions.assertState(c, -1);
		c.doFinal(null);
		//The object is will have an empty state because it was in an error state earlier.
	}

	@Test
	public void testCipher10() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance("AES");
		c.doFinal(null);
		
		Assertions.assertState(c, -1);	
	}

	@Test
	public void testCipher11() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance("AES");
		if(staticallyUnknown())
			c.init(1, new SecretKeySpec(null, "AES"));
		c.doFinal(null);
		Assertions.assertState(c, -1);	
	}
}
