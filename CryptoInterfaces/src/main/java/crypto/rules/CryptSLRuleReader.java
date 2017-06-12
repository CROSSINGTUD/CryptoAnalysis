package crypto.rules;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;


public class CryptSLRuleReader {
	public static CryptSLRule readFromFile(File file) {
		CryptSLRule crytpSLRule = null;
		try {
			FileInputStream fileIn = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			crytpSLRule = (CryptSLRule) in.readObject();
			in.close();
			fileIn.close();		
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return crytpSLRule;
	}
	
}
