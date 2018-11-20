import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import Crypto.PWHasher;

public class Main {

	public static void main(String[] args) throws IOException, GeneralSecurityException {

		///// PASSWORD STORAGE

		// Get password from user via console input
		char[] userinput = new char[256];
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Please enter your password");
		int readChars = br.read(userinput);

		// Copy actual password to separate array to avoid storing a lot of
		// zeros
		char[] userPwd = Arrays.copyOf(userinput, readChars);
		// Clear array
		for (int i = 0; i <= readChars; i++) {
			userinput[i] = 0;
		}

		// TODO: Implement transformation of password userPwd for secure storage
		PWHasher pwHasher = new PWHasher();
		String pwdHash = pwHasher.createPWHash(userPwd);

		System.out.println("Password" + ((pwdHash.isEmpty()) ? " not(!)" : "") + " securely stored.");
		if (pwdHash.isEmpty()) {
			return;
		}
		// Store password in database
		DatabaseConnection.storePassword("Annelie", pwdHash);
		

		///// PASSWORD VERIFICATION

		// Get password from user via console input
		System.out.println("Please enter your password again for confirmation");
		readChars = br.read(userinput);

		// Copy actual password to separate array to avoid storing a lot of
		// zeros
		userPwd = Arrays.copyOf(userinput, readChars);
		// Clear array
		for (int i = 0; i <= readChars; i++) {
			userinput[i] = 0;
		}

		String pwdFromDatabase = DatabaseConnection.retrievePassword("Annelie");

		// TODO: Implement password verification
		Boolean t = pwHasher.verifyPWHash(userPwd, pwdFromDatabase);

		System.out.println("Verification " + ((t) ? "successful" : "failed"));
		
	}

}
