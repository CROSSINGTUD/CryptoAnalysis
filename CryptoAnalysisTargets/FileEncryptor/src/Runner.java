import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;


public class Runner {

	public static void main(String[] args) throws GeneralSecurityException, UnsupportedEncodingException {

		char[] pwd = new char[] { 'p', 'w', 'd' };
		String error = "Reading file failed.";

//		Cipher c = Cipher.getInstance("AES/ABC");
//		c.init(1, KeyGenerator.getInstance("AES").generateKey());
//		error = new String(c.doFinal("WHAT!?".getBytes()));

		FileHandler f = new FileHandler();
		if (!f.readFile(".\\resources\\input.txt")) {
			System.err.println(error);
			System.exit(-1);
		}

		if (!f.encryptContent(pwd)) {
			System.err.println("File not encrypted!");
			System.exit(-2);
		}

		if (f.writeContent(".\\bin\\output.txt")) {
			System.out.println("Encrypted file successfully written!");
		}

	}

}
