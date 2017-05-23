package typestate.tests.crypto;

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

import test.UsagePatternTestingFramework;

public class UsagePatternTest extends UsagePatternTestingFramework{

	@Test
	public void UsagePatternTest1() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Benchmark.extValue(0);
		keygen.init(128);
		Benchmark.extValue(0);
		SecretKey key = keygen.generateKey();
		Benchmark.assertNotErrorState(keygen);
		
		Cipher cCipher = Cipher.getInstance("AES");
		Benchmark.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		Benchmark.extValue(0);
		cCipher.doFinal("".getBytes());
		Benchmark.assertNotErrorState(cCipher);
	}

	@Test
	public void UsagePatternTest2() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Benchmark.extValue(0);
		keygen.init(129);
		Benchmark.extValue(0);
		SecretKey key = keygen.generateKey();
		Benchmark.failedConstraint(keygen);
		Benchmark.assertNotErrorState(keygen);
		
		Cipher cCipher = Cipher.getInstance("AES");
		Benchmark.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		Benchmark.extValue(0);
		cCipher.doFinal("".getBytes());
		Benchmark.assertErrorState(cCipher);
	}

	@Test
	public void UsagePatternTest3() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Benchmark.extValue(0);
		keygen.init(128);
		Benchmark.extValue(0);
		SecretKey key = keygen.generateKey();
		Benchmark.assertNotErrorState(keygen);
		Cipher cCipher = Cipher.getInstance("AES");
		cCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(new byte[18], "AES"));
		Benchmark.extValue(0);
		cCipher.doFinal("".getBytes());
		Benchmark.assertErrorState(cCipher);
		
	}

}
