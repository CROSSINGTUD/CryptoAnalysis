package tests.pattern;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import org.junit.Test;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

public class InputStreamTest extends UsagePatternTestingFramework {

	// Usage Pattern tests for CipherInputStream
	@Test
	public void UsagePatternTestCISDefaultUse() throws GeneralSecurityException, IOException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keyGenerator.init(128);
		Assertions.extValue(0);
		SecretKey key = keyGenerator.generateKey();
		Assertions.hasEnsuredPredicate(key);
		Assertions.mustBeInAcceptingState(keyGenerator);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Assertions.extValue(0);
		cipher.init(Cipher.DECRYPT_MODE, key);
		Assertions.extValue(0);

		InputStream is = Files.newInputStream(Paths.get(".\\resources\\cis.txt"));
		CipherInputStream cis = new CipherInputStream(is, cipher);
		Assertions.extValue(0);
		Assertions.extValue(1);
		while (cis.read() != -1) {
			System.out.println("Reading...");
		}
		cis.close();
		Assertions.mustBeInAcceptingState(cis);
	}

	@Test
	public void UsagePatternTestCISAdditionalUse1() throws GeneralSecurityException, IOException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keyGenerator.init(128);
		Assertions.extValue(0);
		SecretKey key = keyGenerator.generateKey();
		Assertions.hasEnsuredPredicate(key);
		Assertions.mustBeInAcceptingState(keyGenerator);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Assertions.extValue(0);
		cipher.init(Cipher.DECRYPT_MODE, key);
		Assertions.extValue(0);

		InputStream is = Files.newInputStream(Paths.get(".\\resources\\cis.txt"));
		CipherInputStream cis = new CipherInputStream(is, cipher);
		Assertions.extValue(0);
		Assertions.extValue(1);
		int result = cis.read("input".getBytes());
		cis.close();
		Assertions.mustBeInAcceptingState(cis);
		System.out.println(result);
	}

	@Test
	public void UsagePatternTestCISAdditionalUse2() throws GeneralSecurityException, IOException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keyGenerator.init(128);
		Assertions.extValue(0);
		SecretKey key = keyGenerator.generateKey();
		Assertions.hasEnsuredPredicate(key);
		Assertions.mustBeInAcceptingState(keyGenerator);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Assertions.extValue(0);
		cipher.init(Cipher.DECRYPT_MODE, key);
		Assertions.extValue(0);

		InputStream is = Files.newInputStream(Paths.get(".\\resources\\cis.txt"));
		CipherInputStream cis = new CipherInputStream(is, cipher);
		Assertions.extValue(0);
		Assertions.extValue(1);
		int result = cis.read("input".getBytes(), 0, "input".getBytes().length);
		cis.close();
		Assertions.mustBeInAcceptingState(cis);
		System.out.println(result);
	}

	@Test
	public void UsagePatternTestCISMissingCallToClose() throws GeneralSecurityException, IOException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keyGenerator.init(128);
		Assertions.extValue(0);
		SecretKey key = keyGenerator.generateKey();
		Assertions.hasEnsuredPredicate(key);
		Assertions.mustBeInAcceptingState(keyGenerator);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Assertions.extValue(0);
		cipher.init(Cipher.DECRYPT_MODE, key);
		Assertions.extValue(0);

		InputStream is = Files.newInputStream(Paths.get(".\\resources\\cis.txt"));
		CipherInputStream cis = new CipherInputStream(is, cipher);
		Assertions.extValue(0);
		Assertions.extValue(1);
		while (cis.read() != -1) {
			System.out.println("Reading...");
		}
		Assertions.mustNotBeInAcceptingState(cis);
		cis.close();
	}

	@Test
	public void UsagePatternTestCISViolatedConstraint() throws GeneralSecurityException, IOException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keyGenerator.init(128);
		Assertions.extValue(0);
		SecretKey key = keyGenerator.generateKey();
		Assertions.hasEnsuredPredicate(key);
		Assertions.mustBeInAcceptingState(keyGenerator);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Assertions.extValue(0);
		cipher.init(Cipher.DECRYPT_MODE, key);
		Assertions.extValue(0);

		InputStream is = Files.newInputStream(Paths.get(".\\resources\\cis.txt"));
		CipherInputStream cis = new CipherInputStream(is, cipher);
		Assertions.extValue(0);
		Assertions.extValue(1);
		int result = cis.read("input".getBytes(), 100, "input".getBytes().length);
		Assertions.violatedConstraint(cis);
		Assertions.mustNotBeInAcceptingState(cis);
		cis.close();
		System.out.println(result);
	}

	// Usage Pattern tests for DigestInputStream
	@Test
	public void UsagePatternTestDISDefaultUse() throws GeneralSecurityException, IOException {
		InputStream is = Files.newInputStream(Paths.get(".\\resources\\dis.txt"));
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		DigestInputStream dis = new DigestInputStream(is, md);
		Assertions.extValue(0);
		Assertions.extValue(1);
		while (dis.read() != -1) {
			System.out.println("Reading...");
		}
		dis.close();
		Assertions.mustBeInAcceptingState(dis);
	}

	@Test
	public void UsagePatternTestDISAdditionalUse() throws GeneralSecurityException, IOException {
		InputStream is = Files.newInputStream(Paths.get(".\\resources\\dis.txt"));
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		DigestInputStream dis = new DigestInputStream(is, md);
		Assertions.extValue(0);
		Assertions.extValue(1);
		int result = dis.read("input".getBytes(), 0, "input".getBytes().length);
		dis.close();
		Assertions.mustBeInAcceptingState(dis);
		System.out.println(result);
	}

	@Test
	public void UsagePatternTestDISMissingCallToRead() throws GeneralSecurityException, IOException {
		InputStream is = Files.newInputStream(Paths.get(".\\resources\\dis.txt"));
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		DigestInputStream dis = new DigestInputStream(is, md);
		Assertions.extValue(0);
		Assertions.extValue(1);
		Assertions.mustNotBeInAcceptingState(dis);
		while (dis.read() != -1) {
			System.out.println("Reading...");
		}
	}

	@Test
	public void UsagePatternTestDISViolatedConstraint() throws GeneralSecurityException, IOException {
		InputStream is = Files.newInputStream(Paths.get(".\\resources\\dis.txt"));
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		DigestInputStream dis = new DigestInputStream(is, md);
		Assertions.extValue(0);
		Assertions.extValue(1);
		int result = dis.read("input".getBytes(), 100, "input".getBytes().length);
		Assertions.violatedConstraint(dis);
		System.out.println(result);
	}

	@Override
	protected Ruleset getRuleSet() {
		return Ruleset.JavaCryptographicArchitecture;
	}

}
