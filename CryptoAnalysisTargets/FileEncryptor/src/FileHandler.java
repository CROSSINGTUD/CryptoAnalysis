import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;

import javax.crypto.SecretKey;

import Crypto.Enc;
import Crypto.KeyDeriv;

public class FileHandler {

	byte[] content = null;
	byte[] encryptedContent = null;

	public boolean readFile(String path) {
		try {
			content = Files.readAllBytes(Paths.get(path));
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public boolean encryptContent(char[] pwd) throws GeneralSecurityException {
		// TODO: Implement encryption. 'content' must be encrypted, the
		// ciphered text must be stored in 'encryptedContent'.
		
		KeyDeriv kd = new KeyDeriv();
		SecretKey key = kd.getKey(pwd);
		Enc enc = new Enc();
		
		encryptedContent = enc.encrypt(content, key);
		
		return true;
	}

	public boolean writeContent(String path) {
		try {
			Files.write(Paths.get(path), encryptedContent);
		} catch (IOException e) {
			return false;
		} finally {
			content = null;
		}
		return true;
	}
	
}