package crypto.rules;

import java.io.File;
import java.net.MalformedURLException;

import crypto.cryptslhandler.CrySLModelReader;

public class CryptSLRuleReader {

	private static CrySLModelReader csmr;

	private static CrySLModelReader getReader(){
		if (csmr == null)
		{
			try {
				csmr = new CrySLModelReader();
			}
			catch (MalformedURLException e){
				e.printStackTrace();
				// Sebastian:
				// TODO: Current code could cause a NullPointerException
				// Question: Is this Exception ever likely to happen?
				// If no: Swallow it and 'accept' the NullPointerException
				// If it can happen: Maybe we should re-throw a different exception (with this as the inner exception)
				// Reason: In both cases (either NullPointerException or a custom) the creation of CryptSLRules
				// is impossible if the MalformedURLException was thrown. So instead of allowing a generic NullPointerException
				// we could throw something new that could be caught.
			}
		}
		return csmr;
	}

	public static CryptSLRule readFromSourceFile(File file) {
		return getReader().readRule(file);
	}
}
