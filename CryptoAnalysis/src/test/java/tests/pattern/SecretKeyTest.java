package tests.pattern;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.DestroyFailedException;

import org.junit.Test;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class SecretKeyTest extends UsagePatternTestingFramework {

	@Override
	protected Ruleset getRuleSet() {
		return Ruleset.JavaCryptographicArchitecture;
	}
	
	@Test
	public void secretKeyUsagePatternTestReqPredOr() throws GeneralSecurityException {
		SecureRandom secRand = new SecureRandom();
		Assertions.hasEnsuredPredicate(secRand);

		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(128, secRand);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.hasEnsuredPredicate(key);
	}
	
	@Test
	public void secretKeyUsagePatternTest1Simple() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(128);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.hasEnsuredPredicate(key);
		Assertions.mustBeInAcceptingState(keygen);
	}
	
	@Test
	public void secretKeyUsagePattern2() throws GeneralSecurityException, IOException {
		final byte[] salt = new byte[32];
		SecureRandom.getInstanceStrong().nextBytes(salt);

		char[] corPwd = generateRandomPassword();
		final PBEKeySpec pbekeyspec = new PBEKeySpec(corPwd, salt, 65000, 128);
		// Assertions.violatedConstraint(pbekeyspec);
		Assertions.extValue(1);
		Assertions.extValue(2);
		Assertions.extValue(3);
		Assertions.hasEnsuredPredicate(pbekeyspec);
		Assertions.mustNotBeInAcceptingState(pbekeyspec);

		final SecretKeyFactory secFac = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		Assertions.extValue(0);

		final Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
		Assertions.extValue(0);

		SecretKey tmpKey = secFac.generateSecret(pbekeyspec);
		Assertions.mustBeInAcceptingState(secFac);
		pbekeyspec.clearPassword();

		byte[] keyMaterial = tmpKey.getEncoded();
		final SecretKeySpec actKey = new SecretKeySpec(keyMaterial, "AES");
		Assertions.extValue(1);
		Assertions.hasEnsuredPredicate(actKey);

		c.init(Cipher.ENCRYPT_MODE, actKey);
		Assertions.extValue(0);
		Assertions.mustBeInAcceptingState(actKey);

		byte[] encText = c.doFinal("TESTPLAIN".getBytes("UTF-8"));
		c.getIV();

		Assertions.mustBeInAcceptingState(c);
		Assertions.hasEnsuredPredicate(encText);
	}

	@Test
	public void secretKeyUsagePattern3() throws GeneralSecurityException, IOException {
		final byte[] salt = new byte[32];
		SecureRandom.getInstanceStrong().nextBytes(salt);

		final PBEKeySpec pbekeyspec = new PBEKeySpec(generateRandomPassword(), salt, 65000, 128);
		Assertions.extValue(2);
		Assertions.extValue(3);
		Assertions.mustNotBeInAcceptingState(pbekeyspec);

		final SecretKeyFactory secFac = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		Assertions.extValue(0);

		SecretKey tmpKey = secFac.generateSecret(pbekeyspec);
		Assertions.mustBeInAcceptingState(secFac);
		pbekeyspec.clearPassword();

		byte[] keyMaterial = tmpKey.getEncoded();
		final SecretKeySpec actKey = new SecretKeySpec(keyMaterial, "AES");
		Assertions.extValue(1);
		Assertions.hasEnsuredPredicate(actKey);
	}
	
	public char[] generateRandomPassword() {
		SecureRandom rnd = new SecureRandom();
		char[] defaultKey = new char[20];
		for (int i = 0; i < 20; i++) {
			defaultKey[i] = (char) (rnd.nextInt(26) + 'a');
		}
		return defaultKey;
	}
	
	@Test
	public void clearPasswordPredicateTest() throws NoSuchAlgorithmException, GeneralSecurityException {
		Encryption encryption = new Encryption();
		encryption.encryptData(new byte[] {}, "Test");
	}

	public static class Encryption {
		byte[] salt = {15, -12, 94, 0, 12, 3, -65, 73, -1, -84, -35};

		private SecretKey generateKey(String password) throws NoSuchAlgorithmException, GeneralSecurityException {
			PBEKeySpec pBEKeySpec = new PBEKeySpec(password.toCharArray(), salt, 10000, 256);

			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithSHA256");
			Assertions.notHasEnsuredPredicate(pBEKeySpec);
			SecretKey generateSecret = secretKeyFactory.generateSecret(pBEKeySpec);
			Assertions.notHasEnsuredPredicate(generateSecret);
			byte[] keyMaterial = generateSecret.getEncoded();
			Assertions.notHasEnsuredPredicate(keyMaterial);
			SecretKey encryptionKey = new SecretKeySpec(keyMaterial, "AES");
			// pBEKeySpec.clearPassword();
			Assertions.notHasEnsuredPredicate(encryptionKey);
			return encryptionKey;
		}

		private byte[] encrypt(byte[] plainText, SecretKey encryptionKey) throws GeneralSecurityException {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
			return cipher.doFinal(plainText);
		}

		public byte[] encryptData(byte[] plainText, String password) throws NoSuchAlgorithmException, GeneralSecurityException {
			return encrypt(plainText, generateKey(password));
		}
	}
	
	@Test
	public void clearPasswordPredicateTest2() throws NoSuchAlgorithmException, GeneralSecurityException {
		String password = "test";
		byte[] salt = {15, -12, 94, 0, 12, 3, -65, 73, -1, -84, -35};
		PBEKeySpec pBEKeySpec = new PBEKeySpec(password.toCharArray(), salt, 10000, 256);

		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithSHA256");
		Assertions.extValue(0);
		Assertions.notHasEnsuredPredicate(pBEKeySpec);
		SecretKey generateSecret = secretKeyFactory.generateSecret(pBEKeySpec);
		Assertions.notHasEnsuredPredicate(generateSecret);
		byte[] keyMaterial = generateSecret.getEncoded();
		Assertions.notHasEnsuredPredicate(keyMaterial);
	}
	
	@Test
	public void secretKeyTest4() throws NoSuchAlgorithmException, DestroyFailedException {
		KeyGenerator c = KeyGenerator.getInstance("AES");
		Key key = c.generateKey();
		Assertions.mustBeInAcceptingState(key);
		byte[] enc = key.getEncoded();
		Assertions.mustBeInAcceptingState(key);
		enc = key.getEncoded();

		Assertions.mustBeInAcceptingState(key);
		((SecretKey) key).destroy();
		Assertions.mustBeInAcceptingState(key);
	}
	
	@Test
	public void setEntryKeyStore() throws GeneralSecurityException, IOException {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(null, null);
		Assertions.mustBeInAcceptingState(keyStore);

		// Add private and public key (certificate) to keystore
		keyStore.setEntry("alias", null, null);
		keyStore.store(null, "Password".toCharArray());
		Assertions.mustBeInAcceptingState(keyStore);
	}
	
	@Test
	public void secretKeyUsagePatternTest5() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(1);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.notHasEnsuredPredicate(key);
		// Assertions.mustBeInAcceptingState(keygen);
		// Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		// Assertions.extValue(0);
		// cCipher.init(Cipher.ENCRYPT_MODE, key);
		//
		// Assertions.extValue(0);
		// byte[] encText = cCipher.doFinal("".getBytes());
		// Assertions.notHasEnsuredPredicate(encText);
		// Assertions.mustBeInAcceptingState(cCipher);
	}
	
	@Test
	public void secretKeyUsagePatternTest6() throws GeneralSecurityException {
		Encrypter enc = new Encrypter();
		byte[] encText = enc.encrypt("Test");
		Assertions.hasEnsuredPredicate(encText);
	}

	public static class Encrypter {

		Cipher cipher;

		public Encrypter() throws GeneralSecurityException {
			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			keygen.init(128);
			SecretKey key = keygen.generateKey();
			this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			this.cipher.init(Cipher.ENCRYPT_MODE, key);
			this.cipher.getIV();
		}

		public byte[] encrypt(String plainText) throws GeneralSecurityException {
			byte[] encText = this.cipher.doFinal(plainText.getBytes());
			Assertions.hasEnsuredPredicate(encText);
			return encText;
		}
	}
	
	@Test
	public void secretKeyUsagePattern7() throws GeneralSecurityException, IOException {
		final byte[] salt = new byte[32];
		SecureRandom.getInstanceStrong().nextBytes(salt);

		char[] falsePwd = "password".toCharArray();
		final PBEKeySpec pbekeyspec = new PBEKeySpec(falsePwd, salt, 65000, 128);
		Assertions.extValue(0);
		Assertions.extValue(1);
		Assertions.extValue(2);
		Assertions.extValue(3);
		Assertions.notHasEnsuredPredicate(pbekeyspec);
		Assertions.mustNotBeInAcceptingState(pbekeyspec);

		final SecretKeyFactory secFac = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

		final Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
		Assertions.extValue(0);

		SecretKey tmpKey = secFac.generateSecret(pbekeyspec);
		pbekeyspec.clearPassword();

		byte[] keyMaterial = tmpKey.getEncoded();
		final SecretKeySpec actKey = new SecretKeySpec(keyMaterial, "AES");
		Assertions.extValue(1);
		Assertions.notHasEnsuredPredicate(actKey);

		c.init(Cipher.ENCRYPT_MODE, actKey);
		Assertions.extValue(0);
		Assertions.mustBeInAcceptingState(actKey);

		byte[] encText = c.doFinal("TESTPLAIN".getBytes("UTF-8"));
		c.getIV();

		Assertions.mustBeInAcceptingState(c);
		Assertions.notHasEnsuredPredicate(encText);
	}
	
	@Test
	public void exceptionFlowTest() {
		KeyGenerator keygen = null;
		try {
			keygen = KeyGenerator.getInstance("AES");
			Assertions.extValue(0);
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		keygen.init(128);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.mustBeInAcceptingState(keygen);
	}
	
	@Test
	public void secretKeyUsagePatternTestConfigFile() throws GeneralSecurityException, IOException {
		List<String> s = Files.readAllLines(Paths.get("../../../resources/config.txt"));
		KeyGenerator keygen = KeyGenerator.getInstance(s.get(0));
		Assertions.extValue(0);
		keygen.init(128);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.hasEnsuredPredicate(key);
		Assertions.mustBeInAcceptingState(keygen);
	}

}
