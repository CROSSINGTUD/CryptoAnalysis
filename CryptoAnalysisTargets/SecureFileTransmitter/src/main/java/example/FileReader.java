package example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class FileReader {

	String content = null;

	public FileReader(String path) throws IOException {
		content = new String(Files.readAllBytes(Paths.get(path)));
	}

	public String getContent() {
		return content;
	}

}
