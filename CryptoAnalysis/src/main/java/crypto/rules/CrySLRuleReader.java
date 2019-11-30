package crypto.rules;

import java.io.File;
import java.net.MalformedURLException;

import crypto.cryslhandler.CrySLModelReader;

public class CrySLRuleReader {
	private static CrySLModelReader csmr;
	
	public static CrySLRule readFromSourceFile(File file) {
		if(csmr == null)
			try {
				csmr = new CrySLModelReader();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return csmr.readRule(file);
	}
	
}
