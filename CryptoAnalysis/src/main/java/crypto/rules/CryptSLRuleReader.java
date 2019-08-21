package crypto.rules;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;

import crypto.cryptslhandler.CrySLModelReader;

public class CryptSLRuleReader {
	private static CrySLModelReader csmr;
	
	public static CryptSLRule readFromSourceFile(File file) throws MalformedURLException {
		if(csmr == null)
			csmr = new CrySLModelReader();
		return csmr.readRule(file);
	}
	
}
