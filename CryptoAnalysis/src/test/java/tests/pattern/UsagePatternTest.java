package tests.pattern;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.DestroyFailedException;

import org.junit.Test;

import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class UsagePatternTest extends UsagePatternTestingFramework{

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
		
		Cipher cCipher = Cipher.getInstance("AES");
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
		Assertions.mustBeInAcceptingState(cCipher);
		Assertions.hasEnsuredPredicate(encText);
		
		KeyGenerator keygenMac = KeyGenerator.getInstance("HmacSHA256");
		SecretKey keyMac = keygenMac.generateKey();
		
		final Mac hMacSHA256 = Mac.getInstance("HmacSHA256");
		Assertions.extValue(0);
		hMacSHA256.init(keyMac);
		byte[] macced = hMacSHA256.doFinal(msgAsArray);
		Assertions.mustBeInAcceptingState(hMacSHA256);
		Assertions.notHasEnsuredPredicate(macced);
	}
	
	@Test
	public void UsagePatternTest6() throws GeneralSecurityException   {
		SecureRandom keyRand = SecureRandom.getInstanceStrong();
		
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(128, keyRand);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.mustBeInAcceptingState(keygen);
		
		
		SecureRandom encRand = SecureRandom.getInstanceStrong();
		
		Cipher cCipher = Cipher.getInstance("Blowfish");
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key, encRand);
		Assertions.extValue(0);
		byte[] encText = cCipher.doFinal("".getBytes());
		Assertions.mustBeInAcceptingState(cCipher);
		Assertions.notHasEnsuredPredicate(encText);
	}
	
	@Test
	public void UsagePatternTest7() throws GeneralSecurityException   {
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
	public void UsagePattern8() throws GeneralSecurityException, IOException {
		final byte[] salt = new byte[32];
		SecureRandom.getInstanceStrong().nextBytes(salt);

		char[] falsePwd = "password".toCharArray();
		final PBEKeySpec pbekeyspec = new PBEKeySpec(falsePwd, salt, 65000, 128);
//		Assertions.violatedConstraint(pbekeyspec);
		Assertions.extValue(0);
		Assertions.extValue(1);
		Assertions.extValue(2);
		Assertions.extValue(3);
		Assertions.notHasEnsuredPredicate(pbekeyspec);
		Assertions.mustNotBeInAcceptingState(pbekeyspec);
		
		final SecretKeyFactory secFac = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		Assertions.extValue(0);
		
		final Cipher c = Cipher.getInstance("AES/GCM/PKCS5Padding");
		Assertions.extValue(0);
		
		SecretKey tmpKey = secFac.generateSecret(pbekeyspec);
		Assertions.mustBeInAcceptingState(secFac);
		
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
	public void UsagePattern8a() throws GeneralSecurityException, IOException {
		final byte[] salt = new byte[32];
		SecureRandom.getInstanceStrong().nextBytes(salt);

		char [] corPwd = new char[] {'p','a','s','s','w','o','r','d'};
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
		
		final Cipher c = Cipher.getInstance("AES/GCM/PKCS5Padding");
		Assertions.extValue(0);
		
		SecretKey tmpKey = secFac.generateSecret(pbekeyspec);
		Assertions.mustBeInAcceptingState(secFac);
		
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
	public void UsagePattern8b() throws GeneralSecurityException, IOException {
		final byte[] salt = new byte[32];
		SecureRandom.getInstanceStrong().nextBytes(salt);

		final PBEKeySpec pbekeyspec = new PBEKeySpec(new char[] {'p'}, salt, 65000, 128);
		Assertions.extValue(0);
		Assertions.extValue(2);
		Assertions.extValue(3);
		Assertions.mustNotBeInAcceptingState(pbekeyspec);
		
		final SecretKeyFactory secFac = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		Assertions.extValue(0);
		
		SecretKey tmpKey = secFac.generateSecret(pbekeyspec);
		Assertions.mustBeInAcceptingState(secFac);
		
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
		final PBEKeySpec pbekeyspec = new PBEKeySpec(new char[] {'p','a','s','s','w','o','r','d'}, salt, 65000, 128);
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
		Assertions.hasEnsuredPredicate(encText);
		Assertions.mustBeInAcceptingState(cCipher);
	}

	

	@Test
	public void UsagePatternTest11() throws GeneralSecurityException {
		Encrypter enc = new Encrypter();
		byte[] encText = enc.encrypt("Test"); 
		Assertions.hasEnsuredPredicate(encText);
	}
	
	
	public static class Encrypter{
		Cipher cipher;
		public Encrypter() throws GeneralSecurityException{
			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			keygen.init(128);
			SecretKey key = keygen.generateKey();
			this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			this.cipher.init(Cipher.ENCRYPT_MODE, key);
		}
		public byte[] encrypt(String plainText) throws GeneralSecurityException{
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
	}
	
	@Test
	public void UsagePatternTest17() throws GeneralSecurityException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		final byte[] input = "input".getBytes("UTF-8");
		byte[] output = md.digest(input);
		md.reset();
		Assertions.mustNotBeInAcceptingState(md);
		Assertions.hasEnsuredPredicate(input);
		Assertions.hasEnsuredPredicate(output);
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
	public void secretKeyTest() throws NoSuchAlgorithmException, DestroyFailedException{
		KeyGenerator c = KeyGenerator.getInstance("AES");
		SecretKey key = c.generateKey();
		Assertions.mustBeInAcceptingState(key);
		byte[] enc = key.getEncoded();
		Assertions.mustBeInAcceptingState(key);
		enc = key.getEncoded();

		Assertions.mustBeInAcceptingState(key);
		key.destroy();
		Assertions.mustBeInAcceptingState(key);
	}
	
	@Test
	public void messageDigest() throws NoSuchAlgorithmException, DestroyFailedException{
		while(true){
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(new byte[]{});
			md.update(new byte[]{});
			byte[] digest = md.digest();
			Assertions.hasEnsuredPredicate(digest);
		}
	}
}
