package tests.pattern;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.DigestInputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class InputStreamTest extends UsagePatternTestingFramework {

	// Usage Pattern tests for CipherInputStream
	@Test
	public void UsagePatternTestCISDefaultUse()
			throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
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

		InputStream is = new FileInputStream(".\\resources\\cis.txt");
		CipherInputStream cis = new CipherInputStream(is, cipher);
		Assertions.extValue(0);
		Assertions.extValue(1);
		while (cis.read() != -1) {

		}
		cis.close();
		Assertions.mustBeInAcceptingState(cis);
	}

	@Test
	public void UsagePatternTestCISAdditionalUse1()
			throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
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

		InputStream is = new FileInputStream(".\\resources\\cis.txt");
		CipherInputStream cis = new CipherInputStream(is, cipher);
		Assertions.extValue(0);
		Assertions.extValue(1);
		cis.read("input".getBytes());
		cis.close();
		Assertions.mustBeInAcceptingState(cis);
	}

	@Test
	public void UsagePatternTestCISAdditionalUse2()
			throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
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

		InputStream is = new FileInputStream(".\\resources\\cis.txt");
		CipherInputStream cis = new CipherInputStream(is, cipher);
		Assertions.extValue(0);
		Assertions.extValue(1);
		cis.read("input".getBytes(), 0, "input".getBytes().length);
		cis.close();
		Assertions.mustBeInAcceptingState(cis);
	}

	@Test
	public void UsagePatternTestCISMissingCallToClose()
			throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
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

		InputStream is = new FileInputStream(".\\resources\\cis.txt");
		CipherInputStream cis = new CipherInputStream(is, cipher);
		Assertions.extValue(0);
		Assertions.extValue(1);
		while (cis.read() != -1) {

		}
		Assertions.mustNotBeInAcceptingState(cis);
		cis.close();
	}

	@Test
	public void UsagePatternTestCISViolatedConstraint()
			throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
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

		InputStream is = new FileInputStream(".\\resources\\cis.txt");
		CipherInputStream cis = new CipherInputStream(is, cipher);
		Assertions.extValue(0);
		Assertions.extValue(1);
		cis.read("input".getBytes(), 100, "input".getBytes().length);
//					Assertions.violatedConstraint(cis);
		Assertions.mustNotBeInAcceptingState(cis);
		cis.close();
	}

	// Usage Pattern tests for DigestInputStream
	@Test
	public void UsagePatternTestDISDefaultUse()
			throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
		InputStream is = new FileInputStream(".\\resources\\dis.txt");
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		DigestInputStream dis = new DigestInputStream(is, md);
		Assertions.extValue(0);
		Assertions.extValue(1);
		while (dis.read() != -1) {

		}
		dis.close();
		Assertions.mustBeInAcceptingState(dis);
	}

	@Test
	public void UsagePatternTestDISAdditionalUse()
			throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
		InputStream is = new FileInputStream(".\\resources\\dis.txt");
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		DigestInputStream dis = new DigestInputStream(is, md);
		Assertions.extValue(0);
		Assertions.extValue(1);
		dis.read("input".getBytes(), 0, "input".getBytes().length);
		dis.close();
		Assertions.mustBeInAcceptingState(dis);
	}

	@Test
	public void UsagePatternTestDISMissingCallToRead()
			throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
		InputStream is = new FileInputStream(".\\resources\\dis.txt");
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		DigestInputStream dis = new DigestInputStream(is, md);
		Assertions.extValue(0);
		Assertions.extValue(1);
		Assertions.mustNotBeInAcceptingState(dis);
		while (dis.read() != -1) {

		}
	}

	@Test
	public void UsagePatternTestDISViolatedConstraint()
			throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
		InputStream is = new FileInputStream(".\\resources\\dis.txt");
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		DigestInputStream dis = new DigestInputStream(is, md);
		Assertions.extValue(0);
		Assertions.extValue(1);
		dis.read("input".getBytes(), 100, "input".getBytes().length);
		Assertions.violatedConstraint(dis);
	}

	@Override
	protected Ruleset getRuleSet() {
		return Ruleset.JavaCryptographicArchitecture;
	}

}
