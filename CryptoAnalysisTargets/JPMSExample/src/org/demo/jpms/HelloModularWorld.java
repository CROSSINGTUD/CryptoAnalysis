package org.demo.jpms;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public class HelloModularWorld {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
//		System.out.println("Hello, modular World!");
		
		try {
			Cipher instance = Cipher.getInstance("AES/ECB/PKCS5Padding");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
