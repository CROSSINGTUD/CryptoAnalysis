

package Crypto; 

import java.security.InvalidAlgorithmParameterException;

import java.security.InvalidKeyException;

import java.security.NoSuchAlgorithmException;

import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;

import javax.crypto.BadPaddingException;

import javax.crypto.Cipher;

import javax.crypto.IllegalBlockSizeException;

import javax.crypto.NoSuchPaddingException;

import java.security.SecureRandom;

import javax.crypto.spec.IvParameterSpec;

import javax.crypto.spec.SecretKeySpec;

import java.security.spec.InvalidKeySpecException;

import java.util.List;

import java.util.Base64;

import java.io.InputStream;

import java.io.OutputStream;

import java.util.Properties;

import java.io.FileOutputStream;
	
public class Output {
	public void templateUsage(char[] pwd) throws GeneralSecurityException  {
		PWHasher pwHasher = new PWHasher();
		String pwdHash = pwHasher.createPWHash(pwd);
		Boolean t = pwHasher.verifyPWHash(pwd, pwdHash);
	}
}
