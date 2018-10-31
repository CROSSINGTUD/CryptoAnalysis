

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

	public void templateUsage(String host,int port) {
         //You need to set the right host (first parameter) and the port name (second parameter). If you wish to pass a IP address, please use overload with InetAdress as second parameter instead of string.
		 TLSClient tls = new TLSClient(host, port);
		 
		 Boolean sendingSuccessful = tls.sendData("");
		 String data = tls.receiveData();
		
		tls.closeConnection();		
	}
}
