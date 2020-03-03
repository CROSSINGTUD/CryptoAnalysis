package crypto;

import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import crypto.rules.CrySLMethod;
import crypto.rules.CrySLRule;
import soot.SootMethod;

public class Utils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

	public static String getFullyQualifiedName(CrySLRule r) {
		for(CrySLMethod l : r.getUsagePattern().getInitialTransition().getLabel()) {
			return l.toString().substring(0, l.toString().lastIndexOf("."));
		}
		
		LOGGER.error("Could not get fully qualified class name for rule" + r);
		return null;
	}

	public static Collection<String> toSubSignatures(Collection<SootMethod> methods) {
		Set<String> subSignatures = Sets.newHashSet();
		for(SootMethod m : methods){
			subSignatures.add(m.getName());
		}
		return subSignatures;
	}

}
