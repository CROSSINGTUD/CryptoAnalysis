package crypto;

import java.util.Collection;
import java.util.Set;

import com.beust.jcommander.internal.Sets;

import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLRule;
import soot.SootMethod;

public class Utils {

	public static String getFullyQualifiedName(CryptSLRule r) {
		for(CryptSLMethod l : r.getUsagePattern().getInitialTransition().getLabel()) {
			return l.toString().substring(0, l.toString().lastIndexOf("."));
		}
		
		throw new RuntimeException("Could not get fully qualified class name for rule" + r);
	}

	public static Collection<String> toSubSignatures(Collection<SootMethod> methods) {
		Set<String> subSignatures = Sets.newHashSet();
		for(SootMethod m : methods){
			subSignatures.add(m.getName());
		}
		return subSignatures;
	}

}
