package tests.pattern;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.DigestOutputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.Ignore;
import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class OutputStreamTest extends UsagePatternTestingFramework {

	// Usage Pattern for CipherOutputStream
	@Test
	public void UsagePatternTestCOSDefaultUse()
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
		cipher.init(Cipher.ENCRYPT_MODE, key);
		Assertions.extValue(0);

		OutputStream os = new FileOutputStream(".\\resources\\cos.txt");
		CipherOutputStream cos = new CipherOutputStream(os, cipher);
		Assertions.extValue(0);
		Assertions.extValue(1);
		cos.write(new String("Hello World\n").getBytes());
		cos.close();
		Assertions.mustBeInAcceptingState(cos);
	}

	@Test
	public void UsagePatternTestCOSAdditionalUse()
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
		cipher.init(Cipher.ENCRYPT_MODE, key);
		Assertions.extValue(0);

		OutputStream os = new FileOutputStream(".\\resources\\cos.txt");
		CipherOutputStream cos = new CipherOutputStream(os, cipher);
		Assertions.extValue(0);
		Assertions.extValue(1);
		cos.write("message".getBytes(), 0, "message".getBytes().length);
		cos.close();
		Assertions.mustBeInAcceptingState(cos);
	}

	@Test
	public void UsagePatternTestCOSMissingCallToClose()
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
		cipher.init(Cipher.ENCRYPT_MODE, key);
		Assertions.extValue(0);

		OutputStream os = new FileOutputStream(".\\resources\\cos.txt");
		CipherOutputStream cos = new CipherOutputStream(os, cipher);
		Assertions.extValue(0);
		Assertions.extValue(1);
		cos.write(new String("Hello World\n").getBytes());
		Assertions.mustNotBeInAcceptingState(cos);
		cos.close();
	}

	@Test
	public void UsagePatternTestCOSViolatedConstraint()
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
		cipher.init(Cipher.ENCRYPT_MODE, key);
		Assertions.extValue(0);

		OutputStream os = new FileOutputStream(".\\resources\\cos.txt");
		CipherOutputStream cos = new CipherOutputStream(os, cipher);
		Assertions.extValue(0);
		Assertions.extValue(1);
		cos.write("message".getBytes(), 100, "message".getBytes().length);
//			Assertions.violatedConstraint(cos);
		Assertions.mustNotBeInAcceptingState(cos);
		cos.close();
	}

	// Usage Pattern tests for DigestOutputStream
	@Test
	@Ignore
	public void UsagePatternTestDOSCallToForbiddenMethod()
			throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
		OutputStream os = new FileOutputStream(".\\resources\\dos.txt");
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		DigestOutputStream dos = new DigestOutputStream(os, md);
		Assertions.extValue(0);
		Assertions.extValue(1);
		dos.on(false);
		Assertions.callToForbiddenMethod();
		dos.write(new String("Hello World\n").getBytes());
		Assertions.mustBeInAcceptingState(dos);
	}

	@Test
	public void UsagePatternTestDOSMissingCallToWrite()
			throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
		OutputStream os = new FileOutputStream(".\\resources\\dos.txt");
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		DigestOutputStream dos = new DigestOutputStream(os, md);
		Assertions.extValue(0);
		Assertions.extValue(1);
		Assertions.mustNotBeInAcceptingState(dos);
		dos.write(new String("Hello World").getBytes());
	}

	@Test
	public void UsagePatternTestDOSAdditionalUse()
			throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
		OutputStream os = new FileOutputStream(".\\resources\\dos.txt");
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		DigestOutputStream dos = new DigestOutputStream(os, md);
		Assertions.extValue(0);
		Assertions.extValue(1);
		dos.write("message".getBytes(), 0, "message".getBytes().length);
		dos.close();
		Assertions.mustBeInAcceptingState(dos);
	}

	@Test
	public void UsagePatternTestDOSViolatedConstraint()
			throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
		OutputStream os = new FileOutputStream(".\\resources\\dos.txt");
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		DigestOutputStream dos = new DigestOutputStream(os, md);
		Assertions.extValue(0);
		Assertions.extValue(1);
		dos.write("message".getBytes(), 100, "message".getBytes().length);
		Assertions.violatedConstraint(dos);
//				Assertions.mustNotBeInAcceptingState(dos);
	}

	@Override
	protected Ruleset getRuleSet() {
		return Ruleset.JavaCryptographicArchitecture;
	}

}
