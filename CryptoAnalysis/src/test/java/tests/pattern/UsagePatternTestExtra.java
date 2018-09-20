package tests.pattern;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.Test;

import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class UsagePatternTestExtra extends UsagePatternTestingFramework{

	//Usage Pattern tests for DigestInputStream
	@Test
	public void UsagePatternTest23() throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
	  InputStream is = new FileInputStream(".\\resources\\dis.txt");
	  MessageDigest md = MessageDigest.getInstance("SHA-256");
	  Assertions.extValue(0);
	  DigestInputStream dis = new DigestInputStream(is, md);
	  Assertions.extValue(0);
	  Assertions.extValue(1);
	  while (dis.read() != -1) {

	  }
	  Assertions.mustBeInAcceptingState(dis);
	}

	@Test
	public void UsagePatternTest23a() throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
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

//	@Test
//	public void UsagePatternTest23b() throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
//	  InputStream is = new FileInputStream(".\\resources\\dis.txt");
//	  MessageDigest md = MessageDigest.getInstance("SHA-256");
//	  Assertions.extValue(0);
//	  DigestInputStream dis = new DigestInputStream(is, md);
//	  Assertions.extValue(0);
//	  Assertions.extValue(1);
//	  dis.read("input".getBytes(), 0, "input".getBytes().length);
//	  Assertions.mustBeInAcceptingState(dis);
//	}

	@Test
	public void UsagePatternTest23c() throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
	  InputStream is = new FileInputStream(".\\resources\\dis.txt");
	  MessageDigest md = MessageDigest.getInstance("SHA-256");
	  Assertions.extValue(0);
	  DigestInputStream dis = new DigestInputStream(is, md);
	  Assertions.extValue(0);
	  Assertions.extValue(1);
	  dis.read("input".getBytes(), 100, "input".getBytes().length);
	  Assertions.violatedConstraint(dis);
	}

	//Usage Pattern tests for DigestOutputStream
	@Test
	public void UsagePatternTest24() throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
	  OutputStream os = new FileOutputStream(".\\resources\\dos.txt");
	  MessageDigest md = MessageDigest.getInstance("SHA-256");
	  Assertions.extValue(0);
	  DigestOutputStream dos = new DigestOutputStream(os, md);
	  Assertions.extValue(0);
	  Assertions.extValue(1);
	  dos.on(false);
	  Assertions.callToForbiddenMethod();
	  dos.write(new String("Hello World\n").getBytes());
	  Assertions.mustNotBeInAcceptingState(dos);
	}

//	@Test
//	public void UsagePatternTest24a() throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
//	  OutputStream os = new FileOutputStream(".\\resources\\dos.txt");
//	  MessageDigest md = MessageDigest.getInstance("SHA-256");
//	  Assertions.extValue(0);
//	  DigestOutputStream dos = new DigestOutputStream(os, md);
//	  Assertions.extValue(0);
//	  Assertions.extValue(1);
//	  Assertions.mustNotBeInAcceptingState(dos);
//	  dos.write(new String("Hello World").getBytes());
//	}

	@Test
	public void UsagePatternTest24b() throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
	  OutputStream os = new FileOutputStream(".\\resources\\dos.txt");
	  MessageDigest md = MessageDigest.getInstance("SHA-256");
	  Assertions.extValue(0);
	  DigestOutputStream dos = new DigestOutputStream(os, md);
	  Assertions.extValue(0);
	  Assertions.extValue(1);
	  dos.write("message".getBytes(), 0, "message".getBytes().length);
	  Assertions.mustBeInAcceptingState(dos);
	}

//	@Test
//	public void UsagePatternTest24c() throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
//	  OutputStream os = new FileOutputStream(".\\resources\\dos.txt");
//	  MessageDigest md = MessageDigest.getInstance("SHA-256");
//	  Assertions.extValue(0);
//	  DigestOutputStream dos = new DigestOutputStream(os, md);
//	  Assertions.extValue(0);
//	  Assertions.extValue(1);
//	  dos.write("message".getBytes(), 100, "message".getBytes().length);
//	  Assertions.violatedConstraint(dos);
////			Assertions.mustNotBeInAcceptingState(dos);
//	}

	//Usage Pattern for CipherOutputStream
	@Test
	public void UsagePatternTest25() throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
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
	public void UsagePatternTest25a() throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
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

//	@Test
//	public void UsagePatternTest25b() throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
//	  KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//	  Assertions.extValue(0);
//	  keyGenerator.init(128);
//	  Assertions.extValue(0);
//	  SecretKey key = keyGenerator.generateKey();
//	  Assertions.hasEnsuredPredicate(key);
//	  Assertions.mustBeInAcceptingState(keyGenerator);
//	  Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//	  Assertions.extValue(0);
//	  cipher.init(Cipher.ENCRYPT_MODE, key);
//	  Assertions.extValue(0);
//
//	  OutputStream os = new FileOutputStream(".\\resources\\cos.txt");
//	  CipherOutputStream cos = new CipherOutputStream(os, cipher);
//	  Assertions.extValue(0);
//	  Assertions.extValue(1);
//	  cos.write("message".getBytes(), 0, "message".getBytes().length);
//	  cos.close();
//	  Assertions.mustBeInAcceptingState(cos);
//	}

//	@Test
//	public void UsagePatternTest25c() throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
//	  KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//	  Assertions.extValue(0);
//	  keyGenerator.init(128);
//	  Assertions.extValue(0);
//	  SecretKey key = keyGenerator.generateKey();
//	  Assertions.hasEnsuredPredicate(key);
//	  Assertions.mustBeInAcceptingState(keyGenerator);
//	  Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//	  Assertions.extValue(0);
//	  cipher.init(Cipher.ENCRYPT_MODE, key);
//	  Assertions.extValue(0);
//
//	  OutputStream os = new FileOutputStream(".\\resources\\cos.txt");
//	  CipherOutputStream cos = new CipherOutputStream(os, cipher);
//	  Assertions.extValue(0);
//	  Assertions.extValue(1);
//	  cos.write("message".getBytes(), 100, "message".getBytes().length);
////		Assertions.violatedConstraint(cos);
//	  Assertions.mustNotBeInAcceptingState(cos);
//	  cos.close();
//	}

	//Usage Pattern tests for CipherInputStream
//	@Test
//	public void UsagePatternTest26() throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
//	  KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//	  Assertions.extValue(0);
//	  keyGenerator.init(128);
//	  Assertions.extValue(0);
//	  SecretKey key = keyGenerator.generateKey();
//	  Assertions.hasEnsuredPredicate(key);
//	  Assertions.mustBeInAcceptingState(keyGenerator);
//	  Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//	  Assertions.extValue(0);
//	  cipher.init(Cipher.DECRYPT_MODE, key);
//	  Assertions.extValue(0);
//
//	  InputStream is = new FileInputStream(".\\resources\\cis.txt");
//	  CipherInputStream cis = new CipherInputStream(is, cipher);
//	  Assertions.extValue(0);
//	  Assertions.extValue(1);
//	  while (cis.read() != -1) {
//
//	  }
//	  cis.close();
//	  Assertions.mustBeInAcceptingState(cis);
//	}

	@Test
	public void UsagePatternTest26a() throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
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

//	@Test
//	public void UsagePatternTest26b() throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
//	  KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//	  Assertions.extValue(0);
//	  keyGenerator.init(128);
//	  Assertions.extValue(0);
//	  SecretKey key = keyGenerator.generateKey();
//	  Assertions.hasEnsuredPredicate(key);
//	  Assertions.mustBeInAcceptingState(keyGenerator);
//	  Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//	  Assertions.extValue(0);
//	  cipher.init(Cipher.DECRYPT_MODE, key);
//	  Assertions.extValue(0);
//
//	  InputStream is = new FileInputStream(".\\resources\\cis.txt");
//	  CipherInputStream cis = new CipherInputStream(is, cipher);
//	  Assertions.extValue(0);
//	  Assertions.extValue(1);
//	  while (cis.read() != -1) {
//
//	  }
//	  Assertions.mustNotBeInAcceptingState(cis);
//	  cis.close();
//	}

	@Test
	public void UsagePatternTest26c() throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
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
	public void UsagePatternTest26d() throws GeneralSecurityException, UnsupportedEncodingException, FileNotFoundException, IOException {
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
//			Assertions.violatedConstraint(cis);
	  Assertions.mustNotBeInAcceptingState(cis);
	  cis.close();
	}

}	
