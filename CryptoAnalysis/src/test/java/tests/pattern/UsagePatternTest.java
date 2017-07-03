package tests.pattern;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Ignore;
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
		Assertions.assertNotErrorState(keygen);
		Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key);
		
		Assertions.extValue(0);
		byte[] encText = cCipher.doFinal("".getBytes());
		Assertions.hasEnsuredPredicate(encText);
		Assertions.assertNotErrorState(cCipher);
	}

	@Test
	public void UsagePatternTest2() throws GeneralSecurityException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(129);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.assertNotErrorState(keygen);
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
		Assertions.assertNotErrorState(keygen);
		Cipher cCipher = Cipher.getInstance("AES");
		cCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(new byte[18], "AES"));
		Assertions.extValue(0);
		byte[] encText = cCipher.doFinal("".getBytes());
		Assertions.assertNotErrorState(cCipher);
		Assertions.notHasEnsuredPredicate(encText);
	}


	@Test
	public void UsagePatternTest4() throws GeneralSecurityException {
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
		byte[] encText = cCipher.doFinal("".getBytes());
		Assertions.assertNotErrorState(cCipher);
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
		Assertions.assertNotErrorState(keygenEnc);
		
		Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, keyEnc);
		Assertions.extValue(0);
		byte[] encText = cCipher.doFinal(msgAsArray);
		Assertions.assertNotErrorState(cCipher);
		Assertions.hasEnsuredPredicate(encText);
		
		KeyGenerator keygenMac = KeyGenerator.getInstance("HmacSHA256");
		SecretKey keyMac = keygenMac.generateKey();
		
		final Mac hMacSHA256 = Mac.getInstance("HmacSHA256");
		Assertions.extValue(0);
		hMacSHA256.init(keyMac);
		byte[] macced = hMacSHA256.doFinal(msgAsArray);
		Assertions.assertNotErrorState(hMacSHA256);
		Assertions.notHasEnsuredPredicate(macced);
	}
	
	@Test
	public void UsagePatternTest6() throws GeneralSecurityException   {
		SecureRandom keyRand = SecureRandom.getInstanceStrong();
		Assertions.assertNotErrorState(keyRand);
		
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(128, keyRand);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.assertNotErrorState(keygen);
		
		
		SecureRandom encRand = SecureRandom.getInstanceStrong();
		Assertions.assertNotErrorState(encRand);
		
		Cipher cCipher = Cipher.getInstance("Blowfish");
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key, encRand);
		Assertions.extValue(0);
		byte[] encText = cCipher.doFinal("".getBytes());
		Assertions.assertNotErrorState(cCipher);
		Assertions.notHasEnsuredPredicate(encText);
	}
	
	@Test
	public void UsagePatternTest7() throws GeneralSecurityException   {
		SecureRandom rand = SecureRandom.getInstanceStrong();
		Assertions.assertNotErrorState(rand);
		
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		Assertions.extValue(0);
		keygen.init(128);
		Assertions.extValue(0);
		SecretKey key = keygen.generateKey();
		Assertions.assertNotErrorState(keygen);
		
		Cipher cCipher = Cipher.getInstance("Blowfish");
		Assertions.extValue(0);
		cCipher.init(Cipher.ENCRYPT_MODE, key, rand);
		Assertions.extValue(0);
		byte[] encText = cCipher.doFinal("".getBytes());
		Assertions.assertNotErrorState(cCipher);
		Assertions.notHasEnsuredPredicate(encText);
	}
	

	@Test
	public void UsagePattern8() throws GeneralSecurityException, IOException {
		final byte[] salt = new byte[32];
		SecureRandom.getInstanceStrong().nextBytes(salt);

		final PBEKeySpec pbekeyspec = new PBEKeySpec(new char[] {'p','a','s','s','w','o','r','d'}, salt, 65000, 128);
		Assertions.extValue(0);
		Assertions.extValue(2);
		Assertions.extValue(3);
		Assertions.assertErrorState(pbekeyspec);
		
		final SecretKeyFactory secFac = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		Assertions.extValue(0);
		
		final Cipher c = Cipher.getInstance("AES/GCM/PKCS5Padding");
		Assertions.extValue(0);
		
		SecretKey tmpKey = secFac.generateSecret(pbekeyspec);
		Assertions.assertNotErrorState(secFac);
		
		byte[] keyMaterial = tmpKey.getEncoded();
		final SecretKeySpec actKey = new SecretKeySpec(keyMaterial, "AES");
		Assertions.extValue(1);
		
		c.init(Cipher.ENCRYPT_MODE, actKey);
		Assertions.extValue(0);
		Assertions.assertNotErrorState(actKey);
		
		byte[] encText = c.doFinal("TESTPLAIN".getBytes("UTF-8"));
		c.getIV();
		
		Assertions.assertNotErrorState(c);
		Assertions.notHasEnsuredPredicate(encText);
	}

}
