package tests.pattern;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;

import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class UsagePatternTest extends UsagePatternTestingFramework{

	@Test
	public void UsagePatternTest1() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(128);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.assertNotErrorState(keygen);
		
		Cipher cCipher = Cipher.getInstance("AES");
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		Assertions.extValue(0);
		cCipher.doFinal("".getBytes());
		Assertions.assertNotErrorState(cCipher);
	}

	@Test
	public void UsagePatternTest2() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(129);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.assertNotErrorState(keygen);
		Assertions.violatedConstraint(keygen);
		
		Cipher cCipher = Cipher.getInstance("AES");
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		Assertions.extValue(0);
		cCipher.doFinal("".getBytes());
		Assertions.violatedConstraint(cCipher);
	}

	@Test
	public void UsagePatternTest3() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(128);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.assertNotErrorState(keygen);
		Cipher cCipher = Cipher.getInstance("AES");
		cCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(new byte[18], "AES"));
		Assertions.extValue(0);
		cCipher.doFinal("".getBytes());
		Assertions.assertErrorState(cCipher);
		
	}


	@Test
	public void UsagePatternTest4() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(128);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.assertNotErrorState(keygen);
		
		Cipher cCipher = Cipher.getInstance("Blowfish");
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		Assertions.extValue(0);
		cCipher.doFinal("".getBytes());
		Assertions.assertNotErrorState(cCipher);
		Assertions.violatedConstraint(cCipher);
	}

}
