package tests.pattern;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.DestroyFailedException;

import org.junit.Test;

import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class UsagePatternTest extends UsagePatternTestingFramework {

	@Test
	public void useDoFinalInLoop() throws GeneralSecurityException{
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(128);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();;
		Assertions.hasEnsuredPredicate(key);
		Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		Assertions.mustNotBeInAcceptingState(cCipher);
		byte[] enc = null;
		for (int i=0; i<42; i++){
			enc = cCipher.doFinal("".getBytes());
			Assertions.mustBeInAcceptingState(cCipher);
			Assertions.hasEnsuredPredicate(enc);
		}
		Assertions.mustNotBeInAcceptingState(cCipher);
		Assertions.hasEnsuredPredicate(enc);
	}
	
	@Test
	public void caseInsensitiveNames() throws GeneralSecurityException{
		KeyGenerator keygen = KeyGenerator.getInstance("aes");
		Assertions.extValue(0);
		keygen.init(128);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.hasEnsuredPredicate(key);
		Cipher cCipher = Cipher.getInstance("Aes/CbC/pKCS5PADDING");
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] enc = cCipher.doFinal("".getBytes());
		Assertions.mustBeInAcceptingState(cCipher);
		Assertions.hasEnsuredPredicate(enc);
	}
	
	@Test
	public void UsagePatternTest1() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(128);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.hasEnsuredPredicate(key);
		Assertions.mustBeInAcceptingState(keygen);
		Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key);

		Assertions.extValue(0);
		byte[] encText = cCipher.doFinal("".getBytes());
		Assertions.hasEnsuredPredicate(encText);
		Assertions.mustBeInAcceptingState(cCipher);
		cCipher.getIV();
	}
	
	@Test
	public void UsagePatternImprecise() throws GeneralSecurityException {
		SecretKey key = KeyGenerator.getInstance("AES").generateKey();
		Assertions.hasEnsuredPredicate(key);

		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Assertions.extValue(0);
		c.init(Cipher.ENCRYPT_MODE, key);
		byte[] res = c.doFinal("message".getBytes(), 0, "message".getBytes().length);
		Assertions.mustBeInAcceptingState(c);
		Assertions.hasEnsuredPredicate(res);
	}

	@Test
	public void UsagePatternTest1Simple() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(128);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.hasEnsuredPredicate(key);
		Assertions.mustBeInAcceptingState(keygen);
		
	}
	
	@Test
	public void UsagePatternTestInsecureKey() throws GeneralSecurityException {
		byte[] plaintext = "WHAT!?".getBytes();

		SecretKeySpec encKey = new SecretKeySpec(new byte[1] , "AES");
		Assertions.notHasEnsuredPredicate(encKey);
		
		Cipher c = Cipher.getInstance("AES/CBC");
		c.init(1, encKey);
		String ciphertext = new String(c.doFinal(plaintext));
		Assertions.mustBeInAcceptingState(c);
		Assertions.notHasEnsuredPredicate(ciphertext);
	}
	

	@Test
	public void UsagePatternTestInter1() throws GeneralSecurityException {
		SecretKey key = generateKey();
		Assertions.hasEnsuredPredicate(key);
		encrypt(key);
	}

	@Test
	public void UsagePatternTestInter2() throws GeneralSecurityException {
		SecretKey key = generateKey();
		Assertions.hasEnsuredPredicate(key);
		forward(key);
	}

	private void forward(SecretKey key) throws GeneralSecurityException {
		SecretKey tmpKey = key;
		encrypt(tmpKey);
	}

	@Test
	public void UsagePatternTestInter3() throws GeneralSecurityException {
		SecretKey key = generateKey();
		Assertions.hasEnsuredPredicate(key);
		rebuild(key);
	}

	private void rebuild(SecretKey key) throws GeneralSecurityException {
		SecretKey tmpKey = new SecretKeySpec(key.getEncoded(), "AES");
		encrypt(tmpKey);
	}

	@Test
	public void UsagePatternTestInter4() throws GeneralSecurityException {
		SecretKey key = generateKey();
		Assertions.hasEnsuredPredicate(key);
		wrongRebuild(key);
	}

	private void wrongRebuild(SecretKey key) throws GeneralSecurityException {
		SecretKey tmpKey = new SecretKeySpec(key.getEncoded(), "DES");
		Assertions.hasEnsuredPredicate(tmpKey);
		encryptWrong(tmpKey);
	}

	private void encryptWrong(SecretKey key) throws GeneralSecurityException {
		Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key);

		Assertions.extValue(0);
		byte[] encText = cCipher.doFinal("".getBytes());
		Assertions.notHasEnsuredPredicate(encText);
		Assertions.mustBeInAcceptingState(cCipher);
		cCipher.getIV();
	}

	private void encrypt(SecretKey key) throws GeneralSecurityException {
		Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key);

		Assertions.extValue(0);
		byte[] encText = cCipher.doFinal("".getBytes());
		Assertions.hasEnsuredPredicate(encText);
		Assertions.mustBeInAcceptingState(cCipher);
		cCipher.getIV();
	}

	private SecretKey generateKey() throws NoSuchAlgorithmException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(128);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();

		Assertions.mustBeInAcceptingState(keygen);
		return key;
	}

	@Test

	public void exceptionFlowTest() {
		KeyGenerator keygen = null;
		try {
			keygen = KeyGenerator.getInstance("AES");
			Assertions.extValue(0);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		keygen.init(128);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.mustBeInAcceptingState(keygen);
	}


	@Test
	public void UsagePatternTestConfigFile() throws GeneralSecurityException, IOException {
		List<String> s = Files.readAllLines(Paths.get("../../../resources/config.txt"));
		KeyGenerator keygen = KeyGenerator.getInstance(s.get(0));
		Assertions.extValue(0);
		keygen.init(128);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.hasEnsuredPredicate(key);
		Assertions.mustBeInAcceptingState(keygen);
	}

	@Test
	public void UsagePatternTest1SilentForbiddenMethod() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(128);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.hasEnsuredPredicate(key);
		Assertions.mustBeInAcceptingState(keygen);
		Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Assertions.extValue(0);
		cCipher.init(Cipher.DECRYPT_MODE, key);
		Assertions.extValue(0);
		Assertions.callToForbiddenMethod();

		byte[] encText = cCipher.doFinal("".getBytes());
		Assertions.mustBeInAcceptingState(cCipher);
		Assertions.notHasEnsuredPredicate(cCipher);
		cCipher.getIV();
	}

	@Test
	public void UsagePatternTest1a() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(128);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.hasEnsuredPredicate(key);
		Assertions.mustBeInAcceptingState(keygen);

		byte[] iv = new byte[32];
		SecureRandom.getInstanceStrong().nextBytes(iv);
		IvParameterSpec spec = new IvParameterSpec(iv);

		Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Assertions.extValue(0);
		int mode = 1;
		if (Math.random() % 2 == 0) {
			mode = 2;
		}
		cCipher.init(mode, key, spec);

		Assertions.extValue(0);
		byte[] encText = cCipher.doFinal("".getBytes());
		Assertions.hasEnsuredPredicate(encText);
		Assertions.mustBeInAcceptingState(cCipher);
		cCipher.getIV();
	}

	@Test
	public void UsagePatternTestIVCor() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(128);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();

		Assertions.hasEnsuredPredicate(key);
		Assertions.mustBeInAcceptingState(keygen);

		SecureRandom sr = SecureRandom.getInstanceStrong();
		Assertions.hasEnsuredPredicate(sr);

		byte[] ivbytes = new byte[12];
		sr.nextBytes(ivbytes);
		Assertions.hasEnsuredPredicate(ivbytes);

		IvParameterSpec iv = new IvParameterSpec(ivbytes);
		Assertions.mustBeInAcceptingState(iv);
		Assertions.hasEnsuredPredicate(iv);

		Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key, iv);

		Assertions.extValue(0);
		byte[] encText = cCipher.doFinal("".getBytes());
		Assertions.hasEnsuredPredicate(encText);
		Assertions.mustBeInAcceptingState(cCipher);
		cCipher.getIV();
	}

	@Test
	public void UsagePatternTestIVInCor() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(128);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();

		Assertions.hasEnsuredPredicate(key);
		Assertions.mustBeInAcceptingState(keygen);

		byte[] ivbytes = new byte[12];
		Assertions.notHasEnsuredPredicate(ivbytes);

		IvParameterSpec iv = new IvParameterSpec(ivbytes);
		Assertions.mustBeInAcceptingState(iv);
		Assertions.notHasEnsuredPredicate(iv);

		Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key, iv);

		Assertions.extValue(0);
		byte[] encText = cCipher.doFinal("".getBytes());
		Assertions.notHasEnsuredPredicate(encText);
		Assertions.mustBeInAcceptingState(cCipher);
	}

	@Test
	public void UsagePatternTestWrongOffsetSize() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(128);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.hasEnsuredPredicate(key);
		Assertions.mustBeInAcceptingState(keygen);
		
		Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		Assertions.extValue(0);
		final byte[] bytes = "test".getBytes();
		byte[] encText = cCipher.doFinal(bytes, 200, bytes.length);
		Assertions.extValue(0);
		Assertions.extValue(1);
		Assertions.extValue(2);
		//TODO: Fails for reasons different from the ones I expected.
		cCipher.getIV();
		//		Assertions.mustBeInAcceptingState(cCipher);
		//		Assertions.notasEnsuredPredicate(encText);
	}

	@Test
	public void UsagePatternTestMissingMode() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(128);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.hasEnsuredPredicate(key);
		Assertions.mustBeInAcceptingState(keygen);
		Cipher cCipher = Cipher.getInstance("AES");
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key);

		Assertions.extValue(0);
		byte[] encText = cCipher.doFinal("".getBytes());
		Assertions.notHasEnsuredPredicate(encText);
		Assertions.mustBeInAcceptingState(cCipher);
	}

	@Test
	public void UsagePatternTestWrongPadding() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(128);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.hasEnsuredPredicate(key);
		Assertions.mustBeInAcceptingState(keygen);
		Cipher cCipher = Cipher.getInstance("AES/CBC/NoPadding");
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key);

		Assertions.extValue(0);
		byte[] encText = cCipher.doFinal("".getBytes());
		Assertions.notHasEnsuredPredicate(encText);
		Assertions.mustBeInAcceptingState(cCipher);
	}

	@Test
	public void UsagePatternTest2() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(129);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.mustBeInAcceptingState(keygen);
		Assertions.notHasEnsuredPredicate(key);

		Cipher cCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		Assertions.extValue(0);
		byte[] encText = cCipher.doFinal("".getBytes());
		Assertions.notHasEnsuredPredicate(encText);
	}

	@Test
	public void UsagePatternTest3() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(128);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.mustBeInAcceptingState(keygen);
		Cipher cCipher = Cipher.getInstance("AES");
		cCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(new byte[18], "AES"));
		Assertions.extValue(0);
		byte[] encText = cCipher.doFinal("".getBytes());
		Assertions.mustBeInAcceptingState(cCipher);
		Assertions.notHasEnsuredPredicate(encText);
	}

	@Test
	public void UsagePatternTestWrongModeExtraVar() throws GeneralSecurityException {
		String trans = "AES";
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(128);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.mustBeInAcceptingState(keygen);
		Cipher cCipher = Cipher.getInstance(trans);
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		Assertions.extValue(0);
		byte[] encText = cCipher.doFinal("".getBytes());
		Assertions.mustBeInAcceptingState(cCipher);
		Assertions.notHasEnsuredPredicate(encText);
	}

	@Test
	public void UsagePatternTest4() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(128);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.mustBeInAcceptingState(keygen);

		Cipher cCipher = Cipher.getInstance("Blowfish");
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		Assertions.extValue(0);
		byte[] encText = cCipher.doFinal("".getBytes());
		Assertions.mustBeInAcceptingState(cCipher);
		Assertions.notHasEnsuredPredicate(encText);
	}
	
	@Test
	public void UsagePatternTest5() throws GeneralSecurityException {
		final byte[] msgAsArray = "Message".getBytes();

		KeyGenerator keygenEnc = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygenEnc.init(128);
		Assertions.extValue(0);
		SecretKey keyEnc = keygenEnc.generateKey();
		Assertions.mustBeInAcceptingState(keygenEnc);

		Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, keyEnc);
		Assertions.extValue(0);
		byte[] encText = cCipher.doFinal(msgAsArray);
		cCipher.getIV();
		Assertions.mustBeInAcceptingState(cCipher);
		Assertions.hasEnsuredPredicate(encText);

		KeyGenerator keygenMac = KeyGenerator.getInstance("HmacSHA256");
		SecretKey keyMac = keygenMac.generateKey();

		final Mac hMacSHA256 = Mac.getInstance("HmacSHA256");
		Assertions.extValue(0);
		hMacSHA256.init(keyMac);
		byte[] macced = hMacSHA256.doFinal(msgAsArray);
		Assertions.mustBeInAcceptingState(hMacSHA256);
		Assertions.hasEnsuredPredicate(macced);
		//TODO Why doesn't the analysis find the predicate contradiction?
		Assertions.predicateContradiction();
	}

	@Test
	public void UsagePatternTest6() throws GeneralSecurityException {
		SecureRandom keyRand = SecureRandom.getInstanceStrong();

		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		Assertions.hasEnsuredPredicate(keyRand);
		keygen.init(128, keyRand);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.mustBeInAcceptingState(keygen);

		SecureRandom encRand = SecureRandom.getInstanceStrong();

		Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key, encRand);
		Assertions.extValue(0);
		byte[] encText = cCipher.doFinal("".getBytes());
		Assertions.mustBeInAcceptingState(cCipher);
		Assertions.hasEnsuredPredicate(encText);
		cCipher.getIV();
	}

	@Test
	public void UsagePatternTest7() throws GeneralSecurityException {
		SecureRandom rand = SecureRandom.getInstanceStrong();

		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(128);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.mustBeInAcceptingState(keygen);

		Cipher cCipher = Cipher.getInstance("Blowfish");
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key, rand);
		Assertions.extValue(0);
		byte[] encText = cCipher.doFinal("".getBytes());
		Assertions.mustBeInAcceptingState(cCipher);
		Assertions.notHasEnsuredPredicate(encText);
	}

	@Test
	public void UsagePatternTest7b() throws GeneralSecurityException {
		SecureRandom encRand = SecureRandom.getInstanceStrong();

		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(128, null);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.mustBeInAcceptingState(keygen);
		Assertions.notHasEnsuredPredicate(key);

		Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key, encRand);
		Assertions.extValue(0);
		byte[] encText = cCipher.doFinal("".getBytes());
		Assertions.mustBeInAcceptingState(cCipher);
		Assertions.notHasEnsuredPredicate(encText);
	}

	@Test
	public void UsagePatternForbMeth() throws GeneralSecurityException, IOException {
		char[] falsePwd = "password".toCharArray();
		final PBEKeySpec pbekeyspec = new PBEKeySpec(falsePwd);
		Assertions.callToForbiddenMethod();
	}
	@Test
	public void UsagePatternMinPBEIterationsMinimized() throws GeneralSecurityException, IOException {
		final byte[] salt = new byte[32];
		SecureRandom.getInstanceStrong().nextBytes(salt);

		char[] corPwd = new char[] { 'p', 'a', 's', 's', 'w', 'o', 'r', 'd' };
		PBEKeySpec pbekeyspec = new PBEKeySpec(corPwd, salt, 100000, 128);
		Assertions.extValue(1);
	}
	@Test
	public void UsagePatternMinPBEIterations() throws GeneralSecurityException, IOException {
		final byte[] salt = new byte[32];
		SecureRandom.getInstanceStrong().nextBytes(salt);

		char[] corPwd = new char[] { 'p', 'a', 's', 's', 'w', 'o', 'r', 'd' };
		PBEKeySpec pbekeyspec = new PBEKeySpec(corPwd, salt, 100000, 128);
		Assertions.extValue(0);
		Assertions.extValue(1);
		Assertions.extValue(2);
		Assertions.extValue(3);
		Assertions.hasEnsuredPredicate(pbekeyspec);
		Assertions.mustNotBeInAcceptingState(pbekeyspec);
		pbekeyspec.clearPassword();
		pbekeyspec = new PBEKeySpec(corPwd, salt, 9999, 128);
		Assertions.extValue(0);
		Assertions.extValue(1);
		Assertions.extValue(2);
		Assertions.extValue(3);
		Assertions.notHasEnsuredPredicate(pbekeyspec);
		Assertions.mustNotBeInAcceptingState(pbekeyspec);
		pbekeyspec.clearPassword();


		PBEParameterSpec pbeparspec = new PBEParameterSpec(salt, 10000);
		Assertions.extValue(0);
		Assertions.extValue(1);
		Assertions.mustBeInAcceptingState(pbeparspec);
		Assertions.hasEnsuredPredicate(pbeparspec);

		pbeparspec = new PBEParameterSpec(salt, 9999);
		Assertions.extValue(0);
		Assertions.extValue(1);
		Assertions.mustBeInAcceptingState(pbeparspec);
		Assertions.notHasEnsuredPredicate(pbeparspec);

	}

	@Test
	public void UsagePattern8() throws GeneralSecurityException, IOException {
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
	public void UsagePattern8a() throws GeneralSecurityException, IOException {
		final byte[] salt = new byte[32];
		SecureRandom.getInstanceStrong().nextBytes(salt);

		char[] corPwd = new char[] { 'p', 'a', 's', 's', 'w', 'o', 'r', 'd' };
		final PBEKeySpec pbekeyspec = new PBEKeySpec(corPwd, salt, 65000, 128);
		//		Assertions.violatedConstraint(pbekeyspec);
		Assertions.extValue(0);
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
	public void UsagePattern8c() throws GeneralSecurityException, IOException {
		final byte[] salt = new byte[32];
		SecureRandom.getInstanceStrong().nextBytes(salt);

		Assertions.hasEnsuredPredicate(salt);
		char[] corPwd = new char[] { 'p', 'a', 's', 's', 'w', 'o', 'r', 'd' };
		final PBEKeySpec pbekeyspec = new PBEKeySpec(corPwd, salt, 65000, 128);
		//		Assertions.violatedConstraint(pbekeyspec);
		Assertions.extValue(0);
		Assertions.extValue(1);
		Assertions.extValue(2);
		Assertions.extValue(3);
		Assertions.hasEnsuredPredicate(pbekeyspec);
		Assertions.mustNotBeInAcceptingState(pbekeyspec);
		pbekeyspec.clearPassword();
	}

	@Test
	public void UsagePattern8b() throws GeneralSecurityException, IOException {
		final byte[] salt = new byte[32];
		SecureRandom.getInstanceStrong().nextBytes(salt);

		final PBEKeySpec pbekeyspec = new PBEKeySpec(new char[] { 'p' }, salt, 65000, 128);
		Assertions.extValue(0);
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

	@Test
	public void UsagePattern9() throws GeneralSecurityException, IOException {
		final byte[] salt = new byte[32];
		SecureRandom.getInstanceStrong().nextBytes(salt);
		Assertions.hasEnsuredPredicate(salt);
		final PBEKeySpec pbekeyspec = new PBEKeySpec(new char[] { 'p', 'a', 's', 's', 'w', 'o', 'r', 'd' }, salt, 65000, 128);
		Assertions.extValue(0);
		Assertions.extValue(2);
		Assertions.extValue(3);
		Assertions.mustNotBeInAcceptingState(pbekeyspec);
		Assertions.hasEnsuredPredicate(pbekeyspec);
		pbekeyspec.clearPassword();
		Assertions.notHasEnsuredPredicate(pbekeyspec);
	}

	@Test
	public void UsagePatternTest10() throws GeneralSecurityException {
		String aesString = "AES";
		KeyGenerator keygen = KeyGenerator.getInstance(aesString);
		Assertions.extValue(0);
		int keySize = 128;
		int a = keySize;
		keygen.init(a);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.hasEnsuredPredicate(key);
		Assertions.mustBeInAcceptingState(keygen);
		Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		Assertions.extValue(0);
		byte[] encText = cCipher.doFinal("".getBytes());
		cCipher.getIV();
		Assertions.hasEnsuredPredicate(encText);
		Assertions.mustBeInAcceptingState(cCipher);
	}

	@Test
	public void UsagePatternTest11() throws GeneralSecurityException {
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
	public void UsagePatternTest12() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(1);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.notHasEnsuredPredicate(key);
		//		Assertions.mustBeInAcceptingState(keygen);
		//		Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		//		Assertions.extValue(0);
		//		cCipher.init(Cipher.ENCRYPT_MODE, key);
		//		
		//		Assertions.extValue(0);
		//		byte[] encText = cCipher.doFinal("".getBytes());
		//		Assertions.notHasEnsuredPredicate(encText);
		//		Assertions.mustBeInAcceptingState(cCipher);
	}

	@Test
	public void UsagePatternTest13() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(1);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.notHasEnsuredPredicate(key);
		Assertions.mustBeInAcceptingState(keygen);
		Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key);

		Assertions.extValue(0);
		byte[] encText = cCipher.doFinal("".getBytes());
		Assertions.notHasEnsuredPredicate(encText);
		Assertions.mustBeInAcceptingState(cCipher);
	}

	@Test
	public void UsagePatternTest14() throws GeneralSecurityException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		final byte[] input = "input".getBytes("UTF-8");
		byte[] output = md.digest(input);
		Assertions.mustBeInAcceptingState(md);
		Assertions.hasEnsuredPredicate(input);
		Assertions.hasEnsuredPredicate(output);
	}

	@Test
	public void UsagePatternTest15() throws GeneralSecurityException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		Assertions.extValue(0);
		final byte[] input = "input".getBytes("UTF-8");
		byte[] output = md.digest(input);
		Assertions.mustBeInAcceptingState(md);
		Assertions.notHasEnsuredPredicate(input);
		Assertions.notHasEnsuredPredicate(output);
		Assertions.violatedConstraint(md);
	}

	@Test
	public void UsagePatternTest16() throws GeneralSecurityException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		final byte[] input = "input".getBytes("UTF-8");
		md.update(input);
		Assertions.mustNotBeInAcceptingState(md);
		Assertions.notHasEnsuredPredicate(input);
		md.digest();
	}

	@Test
	public void UsagePatternTest16b() throws GeneralSecurityException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		final byte[] input = "input".getBytes("UTF-8");
		md.update(input);
		byte[] digest = md.digest();
		Assertions.mustBeInAcceptingState(md);
		Assertions.hasEnsuredPredicate(digest);
	}

	@Test
	public void UsagePatternTest16c() throws GeneralSecurityException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		final String[] input = { "input1", "input2", "input3", "input4" };
		int i = 0;
		while (i < input.length) {
			md.update(input[i].getBytes("UTF-8"));
		}
		byte[] digest = md.digest();
		Assertions.mustBeInAcceptingState(md);
		Assertions.hasEnsuredPredicate(digest);
	}

	@Test
	public void UsagePatternTest17() throws GeneralSecurityException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		final byte[] input = "input".getBytes("UTF-8");
		byte[] output = md.digest(input);
		md.reset();
		Assertions.mustBeInAcceptingState(md);
		Assertions.hasEnsuredPredicate(input);
		Assertions.hasEnsuredPredicate(output);
		md.digest();
	}

	@Test
	public void UsagePatternTest18() throws GeneralSecurityException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		final byte[] input = "input".getBytes("UTF-8");
		byte[] output = md.digest(input);
		Assertions.hasEnsuredPredicate(input);
		Assertions.hasEnsuredPredicate(output);
		output = null;
		Assertions.notHasEnsuredPredicate(output);
		md.reset();
		output = md.digest(input);
		Assertions.mustBeInAcceptingState(md);
		Assertions.hasEnsuredPredicate(input);
		Assertions.hasEnsuredPredicate(output);
	}

	@Test
	public void UsagePatternTest19() throws GeneralSecurityException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		final byte[] input = "input".getBytes("UTF-8");
		final byte[] input2 = "input2".getBytes("UTF-8");
		byte[] output = md.digest(input);
		Assertions.hasEnsuredPredicate(input);
		Assertions.hasEnsuredPredicate(output);
		md.reset();
		md.update(input2);
		Assertions.mustNotBeInAcceptingState(md);
		Assertions.notHasEnsuredPredicate(input2);
		Assertions.hasEnsuredPredicate(output);
		md.digest();
	}

	@Test
	public void UsagePatternTest20() throws GeneralSecurityException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		final byte[] input = "input".getBytes("UTF-8");
		final byte[] input2 = "input2".getBytes("UTF-8");
		byte[] output = md.digest(input);
		Assertions.hasEnsuredPredicate(input);
		Assertions.hasEnsuredPredicate(output);
		Assertions.mustBeInAcceptingState(md);

		md = MessageDigest.getInstance("MD5");
		output = md.digest(input2);
		Assertions.mustBeInAcceptingState(md);
		Assertions.notHasEnsuredPredicate(input2);
		Assertions.notHasEnsuredPredicate(output);
	}

	@Test
	public void UsagePatternTest21() throws GeneralSecurityException, UnsupportedEncodingException {
		String input = "TESTITESTiTEsTI";

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
		Assertions.extValue(0);
		keyGen.initialize(2048);
		KeyPair kp = keyGen.generateKeyPair();
		Assertions.mustBeInAcceptingState(keyGen);
		Assertions.hasEnsuredPredicate(kp);

		final PrivateKey privKey = kp.getPrivate();
		Assertions.hasEnsuredPredicate(privKey);
		Signature sign = Signature.getInstance("SHA256withDSA");
		Assertions.extValue(0);

		sign.initSign(privKey);
		sign.update(input.getBytes("UTF-8"));
		byte[] signature = sign.sign();
		Assertions.mustBeInAcceptingState(sign);
		Assertions.hasEnsuredPredicate(signature);

	}

	@Test
	public void UsagePatternTest21a() throws GeneralSecurityException, UnsupportedEncodingException {
		String input = "TESTITESTiTEsTI";

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
		Assertions.extValue(0);
		keyGen.initialize(2048);
		KeyPair kp = keyGen.generateKeyPair();
		Assertions.mustBeInAcceptingState(keyGen);
		Assertions.hasEnsuredPredicate(kp);

		final PrivateKey privKey = kp.getPrivate();
		Assertions.hasEnsuredPredicate(privKey);
		String algorithm = "SHA256withDSA";
		if (Math.random() % 2 == 0) {
			algorithm = "SHA256withECDSA";
		}
		Signature sign = Signature.getInstance(algorithm);
		Assertions.extValue(0);

		sign.initSign(privKey);
		sign.update(input.getBytes("UTF-8"));
		byte[] signature = sign.sign();
		Assertions.mustBeInAcceptingState(sign);
		Assertions.hasEnsuredPredicate(signature);

	}

	@Test
	public void UsagePatternTest22() throws GeneralSecurityException, UnsupportedEncodingException {
		String input = "TESTITESTiTEsTI";

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
		Assertions.extValue(0);
		keyGen.initialize(2048);
		KeyPair kp = keyGen.generateKeyPair();
		Assertions.mustBeInAcceptingState(keyGen);
		Assertions.hasEnsuredPredicate(kp);

		final PrivateKey privKey = kp.getPrivate();
		Assertions.mustBeInAcceptingState(kp);
		Assertions.hasEnsuredPredicate(privKey);
		Signature sign = Signature.getInstance("SHA256withDSA");
		Assertions.extValue(0);

		sign.initSign(privKey);
		sign.update(input.getBytes("UTF-8"));
		byte[] signature = sign.sign();
		Assertions.mustBeInAcceptingState(sign);
		Assertions.hasEnsuredPredicate(signature);

		final PublicKey pubKey = kp.getPublic();
		Assertions.mustBeInAcceptingState(kp);
		Assertions.hasEnsuredPredicate(pubKey);

		Signature ver = Signature.getInstance("SHA256withDSA");
		Assertions.extValue(0);
		//		
		ver.initVerify(pubKey);
		ver.update(input.getBytes("UTF-8"));
		ver.verify(signature);
		Assertions.mustBeInAcceptingState(ver);
	}

	@Test
	public void secretKeyTest() throws NoSuchAlgorithmException, DestroyFailedException {
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
	public void messageDigest() throws NoSuchAlgorithmException, DestroyFailedException {
		while (true) {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(new byte[] {});
			md.update(new byte[] {});
			byte[] digest = md.digest();
			Assertions.hasEnsuredPredicate(digest);
		}
	}
	
	@Test
	public void messageDigestReturned() throws NoSuchAlgorithmException, DestroyFailedException {
		MessageDigest d = createDigest();
		byte[] digest = d.digest(new byte[] {});
		Assertions.hasEnsuredPredicate(digest);
		Assertions.typestateErrors(0);
	}

	private MessageDigest createDigest() throws NoSuchAlgorithmException {
		return MessageDigest.getInstance("SHA-256");
	}
	
	@Test
	public void clearPasswordPredicateTest() throws NoSuchAlgorithmException, GeneralSecurityException {
		Encryption encryption = new Encryption();
		encryption.encryptData(new  byte[] {}, "Test");
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
			  //pBEKeySpec.clearPassword();
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
	public void setEntryKeyStore() throws GeneralSecurityException, IOException {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(null,null);
		Assertions.mustBeInAcceptingState(keyStore);
		
		// Add private and public key (certificate) to keystore
		keyStore.setEntry("alias", null, null);
		keyStore.store(null, "Password".toCharArray());
		Assertions.mustBeInAcceptingState(keyStore);
		
	}
	
	@Test
	public void negativeRsaParameterSpecTest() throws GeneralSecurityException, IOException {
		Integer keySize = new Integer(102);
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		RSAKeyGenParameterSpec parameters = new RSAKeyGenParameterSpec(keySize, RSAKeyGenParameterSpec.F4);
		Assertions.notHasEnsuredPredicate(parameters);
		Assertions.extValue(0);
		generator.initialize(parameters, new SecureRandom());
		KeyPair keyPair = generator.generateKeyPair();
		Assertions.notHasEnsuredPredicate(keyPair);
	}
	
	@Test
	public void positiveRsaParameterSpecTest() throws GeneralSecurityException, IOException {
		Integer keySize = new Integer(2048);
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		RSAKeyGenParameterSpec parameters = new RSAKeyGenParameterSpec(keySize, RSAKeyGenParameterSpec.F4);
		Assertions.extValue(0);
		Assertions.extValue(1);
		Assertions.hasEnsuredPredicate(parameters);
		generator.initialize(parameters, new SecureRandom());
		KeyPair keyPair = generator.generateKeyPair();
		Assertions.hasEnsuredPredicate(keyPair);
	}

	@Test
	public void positiveRsaParameterSpecTestBigInteger() throws GeneralSecurityException, IOException {
		Integer keySize = new Integer(2048);
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		RSAKeyGenParameterSpec parameters = new RSAKeyGenParameterSpec(keySize, BigInteger.valueOf(65537));
		Assertions.extValue(0);
		Assertions.extValue(1);
		Assertions.hasEnsuredPredicate(parameters);
		generator.initialize(parameters, new SecureRandom());
		KeyPair keyPair = generator.generateKeyPair();
		Assertions.hasEnsuredPredicate(keyPair);
	}
	
	@Test
	public void testSignature() throws InvalidKeyException, GeneralSecurityException {
		Signature s = Signature.getInstance("SHA256withRSA");
//		no initSign call
		s.update("".getBytes());
		s.sign();
		Assertions.notHasEnsuredPredicate(s); 
		Assertions.mustNotBeInAcceptingState(s); 
	}
}
