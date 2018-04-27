package crypto;

import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLRule;

public class Utils {

	public static String getFullyQualifiedName(CryptSLRule r) {
		for(CryptSLMethod l : r.getUsagePattern().getInitialTransition().getLabel()) {
			return l.toString().substring(0, l.toString().lastIndexOf("."));
		}
		
		throw new RuntimeException("Could not get fully qualified class name for rule" + r);
	}

}
