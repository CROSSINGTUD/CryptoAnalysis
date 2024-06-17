package tests.endoflifecycle;

import org.junit.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.security.auth.DestroyFailedException;
import java.security.GeneralSecurityException;

/**
 * Created by johannesspath on 24.12.17.
 */
public class EndOfLifeCycleErrorTest extends UsagePatternTestingFramework {

	@Override
	protected String getRulesetPath() {
		return TestConstants.JCA_RULESET_PATH;
	}

	@Test
	public void missingDoFinalCall() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		keygen.init(128);
		SecretKey key = keygen.generateKey();
		Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		Assertions.missingTypestateChange();
	}

	@Test
	public void missingGenerateKey() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		keygen.init(128);
		Assertions.missingTypestateChange();
	}

	@Test
	public void missingGenerateKeyCaught() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		keygen.init(128);
		Assertions.missingTypestateChange();
	}

	@Test
	public void missingDoFinalCall2() throws GeneralSecurityException, DestroyFailedException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		keygen.init(128);
		SecretKey key = keygen.generateKey();
		Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		Assertions.missingTypestateChange();
		key.destroy();
	}

	@Test
	public void missingDoFinalCall3() throws GeneralSecurityException, DestroyFailedException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		keygen.init(128);
		SecretKey key = keygen.generateKey();
		Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Container con = new Container();
		con.c = cCipher;
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		Cipher cipher = con.c;
		cipher.getAlgorithm();
		Assertions.missingTypestateChange();
		key.destroy();
	}

	// TODO Boomerang Error
	@Test
	public void missingDoFinalCall5() throws GeneralSecurityException, DestroyFailedException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		keygen.init(128);
		SecretKey key = keygen.generateKey();
		Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Container con = new Container();
		con.c = cCipher;
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		Cipher cipher = con.c;
		cipher.doFinal(null);
		Assertions.noMissingTypestateChange();
		cipher.getAlgorithm();
		key.destroy();
	}

	private static class Container {
		Cipher c;
	}
}
