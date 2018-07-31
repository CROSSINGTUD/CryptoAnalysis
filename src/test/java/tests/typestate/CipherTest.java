package tests.typestate;

import java.io.File;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;

import test.IDEALCrossingTestingFramework;
import test.assertions.Assertions;

public class CipherTest extends IDEALCrossingTestingFramework {

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
		int x = 1;
		c.init(1, new SecretKeySpec(null, "AES"));
		int y = 1;

		Assertions.assertState(c, 1);
	}
	@Test
	public void testCipher2a() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher c = Cipher.getInstance("AES");
		c.init(1, new SecretKeySpec(null, "AES"));
		Cipher b = c;

		Assertions.assertState(b, 1);
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
		if (staticallyUnknown())
			c.init(1, new SecretKeySpec(null, "AES"));
		c.doFinal(null);
		Assertions.assertState(c, -1);
	}

	@Test
	public void testCipher12Aliasing() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance("AES");
		Cipher e = c;
		c.doFinal(null);
		Assertions.assertState(e, -1);
	}

	@Test
	public void testCipher13Aliasing() throws GeneralSecurityException {
		Encrypter enc = new Encrypter();
		Assertions.assertState(enc.cipher, 1);
	}

	public static class Encrypter {

		Cipher cipher;

		public Encrypter() throws GeneralSecurityException {
			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			keygen.init(128);
			SecretKey key = keygen.generateKey();
			this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			this.cipher.init(Cipher.ENCRYPT_MODE, key);
			Assertions.assertState(this.cipher, 1);
		}

		public byte[] encrypt(String plainText) throws GeneralSecurityException {
			byte[] encText = this.cipher.doFinal(plainText.getBytes());
			Assertions.hasEnsuredPredicate(encText);
			return encText;
		}
	}
}
