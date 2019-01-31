package MessageDigestExample.MessageDigestExample;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Main {

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		
		File file = new File(".\\resources\\abc.txt");
		InputStream inputstream = new FileInputStream(file);
		String msg = getSHA256(inputstream);
		System.out.println(msg);

	}
	
	private static String getSHA256(InputStream uri) throws IOException, NoSuchAlgorithmException
    {
        InputStream is = uri;
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        DigestInputStream dis = new DigestInputStream(is, md);

        while (dis.read() != -1) {
            // we just drain the stream here to compute the Message Digest
        }
        
        md = dis.getMessageDigest();

        StringBuilder sb = new StringBuilder(64); // SHA-256 is always 64 hex characters
        for (byte b : md.digest())
        {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
