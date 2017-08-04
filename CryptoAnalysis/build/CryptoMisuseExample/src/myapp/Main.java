package myapp;

import java.security.GeneralSecurityException;

public class Main {

	public static void main(String[] args) throws GeneralSecurityException {
		Encrypter encrypter = new Encrypter();
		byte[] encrypt = encrypter.encrypt("Encrypt me");
	}

}
