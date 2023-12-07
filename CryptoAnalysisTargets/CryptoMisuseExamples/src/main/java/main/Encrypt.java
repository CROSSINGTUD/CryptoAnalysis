package main;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.RSAKeyGenParameterSpec;


public class Encrypt {
	public void correct() throws GeneralSecurityException {
		int keySize = 4096;
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		RSAKeyGenParameterSpec parameters = new RSAKeyGenParameterSpec(keySize, RSAKeyGenParameterSpec.F4);
		generator.initialize(parameters, new SecureRandom());
		KeyPair keyPair = generator.generateKeyPair();
	}
	public void incorrect() throws GeneralSecurityException {
		// Since 3.0.0: key size of 2048 is not allowed
		int keySize = 2048;
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		RSAKeyGenParameterSpec parameters = new RSAKeyGenParameterSpec(keySize, RSAKeyGenParameterSpec.F0);
		generator.initialize(parameters, new SecureRandom());
		KeyPair keyPair = generator.generateKeyPair();
	}
	public void correctBigInteger() throws GeneralSecurityException {
		int keySize = 4096;
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		RSAKeyGenParameterSpec parameters = new RSAKeyGenParameterSpec(keySize, BigInteger.valueOf(65537));
		generator.initialize(parameters, new SecureRandom());
		KeyPair keyPair = generator.generateKeyPair();
	}
	public void incorrectBigInteger() throws GeneralSecurityException {
		// Since 3.0.0: key size of 2048 is not allowed
		int keySize = 2048;
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		RSAKeyGenParameterSpec parameters = new RSAKeyGenParameterSpec(keySize, BigInteger.valueOf(2));
		generator.initialize(parameters, new SecureRandom());
		KeyPair keyPair = generator.generateKeyPair();
	}
}
