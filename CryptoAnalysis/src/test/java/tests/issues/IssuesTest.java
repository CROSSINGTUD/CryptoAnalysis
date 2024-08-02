package tests.issues;

import org.junit.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.security.auth.DestroyFailedException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;

public class IssuesTest extends UsagePatternTestingFramework {

	@Override
	protected String getRulesetPath() {
		return TestConstants.JCA_RULESET_PATH;
	}
	
	@Test
	public void testIssue418() throws GeneralSecurityException {
		// Related to issue 418: https://github.com/CROSSINGTUD/CryptoAnalysis/issues/418
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		Assertions.hasEnsuredPredicate(sr);

		byte[] secureBytes = new byte[32];
		(new SecureRandom()).nextBytes(secureBytes);
		Assertions.hasEnsuredPredicate(secureBytes);

		sr.setSeed(secureBytes);

		Assertions.hasEnsuredPredicate(sr);
		Assertions.predicateErrors(0);
		Assertions.constraintErrors(0);
		Assertions.typestateErrors(0);
	}
	
	@Test
	public void testIssue419() throws GeneralSecurityException, DestroyFailedException {
		// Related to issue 419: https://github.com/CROSSINGTUD/CryptoAnalysis/issues/419
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		SecretKey secretKey = keyGenerator.generateKey();
		Assertions.hasEnsuredPredicate(secretKey);
		
		secretKey.destroy();
		Assertions.notHasEnsuredPredicate(secretKey);

		// generate secure iv
		byte[] ivBytes = new byte[16];
		new SecureRandom().nextBytes(ivBytes);
		IvParameterSpec iv = new IvParameterSpec(ivBytes);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

		cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

		// encrypt
		byte[] plainText = "ThisIsThePlainText".getBytes(StandardCharsets.UTF_8);
		byte[] cipherText = cipher.doFinal(plainText);
		Assertions.notHasEnsuredPredicate(cipherText);
	}
	
	@Test
	public void testIssue421() throws GeneralSecurityException {
		// Related to issue 421: https://github.com/CROSSINGTUD/CryptoAnalysis/issues/421
		X509EncodedKeySpec keySpec1 = new X509EncodedKeySpec("insecureKeyBytes".getBytes());
		X509EncodedKeySpec keySpec2 = new X509EncodedKeySpec("insecureKeyBytes".getBytes());

		Assertions.notHasEnsuredPredicate(keySpec1);
		Assertions.notHasEnsuredPredicate(keySpec2);

		KeyFactory kf = KeyFactory.getInstance("RSA");
		PublicKey pubKey1 = kf.generatePublic(keySpec1);
		Assertions.notHasEnsuredPredicate(pubKey1);
		
		PublicKey pubKey2 = kf.generatePublic(keySpec2);
		Assertions.notHasEnsuredPredicate(pubKey2);

		Assertions.predicateErrors(6);
	}

}
