package crypto;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import soot.SootMethod;

public class Utils {
	
	public static Collection<String> toSubSignatures(Collection<SootMethod> methods) {
		Set<String> subSignatures = Sets.newHashSet();
		for(SootMethod m : methods){
			subSignatures.add(m.getName());
		}
		return subSignatures;
	}

}
