import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

	public boolean encryptContent(char[] pwd) {
		// TODO: Implement encryption. 'content' must be encrypted, the
		// ciphertext must be stored in 'encryptedContent.
		
		return false;
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