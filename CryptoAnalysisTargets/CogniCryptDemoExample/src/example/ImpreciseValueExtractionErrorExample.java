package example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

public class ImpreciseValueExtractionErrorExample {
	public static boolean NATIVE = false;
	public static void main(String... args) throws GeneralSecurityException, NoSuchPaddingException, IOException {
		String transformation;
		if(NATIVE) {
			transformation = readFromNative();
		} else {
			transformation = readFromConfigFile();
		}
		Cipher instance = Cipher.getInstance(transformation);
		instance.init(Cipher.ENCRYPT_MODE, getKey());
		instance.doFinal(args[0].getBytes());
	}

	private static Key getKey() throws GeneralSecurityException {
		return KeyGenerator.getInstance("AES").generateKey();
	}

	private static native String readFromNative();
	
	private static String readFromConfigFile() throws FileNotFoundException, IOException {
		return new BufferedReader(new FileReader(new File(""))).readLine();
	}

}
