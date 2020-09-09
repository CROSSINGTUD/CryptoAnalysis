package crypto.rules;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.google.common.collect.Sets;

import crypto.interfaces.ISLConstraint;
import soot.SootMethod;

public class CrySLRule implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String className;
	
	private final List<Entry<String, String>> objects;  
	
	protected final List<CrySLForbiddenMethod> forbiddenMethods;
	
	protected final StateMachineGraph usagePattern;
	
	protected final List<ISLConstraint> constraints;
	
	protected final List<CrySLPredicate> predicates;
	
	public CrySLRule(String _className, List<Entry<String, String>> defObjects, List<CrySLForbiddenMethod> _forbiddenMethods, StateMachineGraph _usagePattern, List<ISLConstraint> _constraints, List<CrySLPredicate> _predicates) {
		className = _className;
		objects = defObjects;
		forbiddenMethods = _forbiddenMethods;
		usagePattern = _usagePattern;
		constraints = _constraints;
		predicates = _predicates;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CrySLRule) {
			return ((CrySLRule) obj).getClassName().equals(className);
		} 
		return false;
	}


	public boolean isLeafRule() {
		for (ISLConstraint con : constraints) {
			if (con instanceof CrySLPredicate) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return 31 * className.hashCode();
	}


	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	
	/**
	 * @return the objects
	 */
	public List<Entry<String, String>> getObjects() {
		return objects;
	}
	
	/**
	 * @return the forbiddenMethods
	 */
	public List<CrySLForbiddenMethod> getForbiddenMethods() {
		return forbiddenMethods;
	}
	
	/**
	 * @return the usagePattern
	 */
	public StateMachineGraph getUsagePattern() {
		return usagePattern;
	}
	
	/**
	 * @return the constraints
	 */
	public List<ISLConstraint> getConstraints() {
		return constraints;
	}
	
	/**
	 * @return the predicates
	 */
	public List<CrySLPredicate> getPredicates() {
		return predicates;
	}
	/**
	 * @return the constraints
	 */
	public List<CrySLPredicate> getRequiredPredicates() {
		List<CrySLPredicate> requires = new LinkedList<CrySLPredicate>();
		for (ISLConstraint con : constraints) {
			if (con instanceof CrySLPredicate) {
				requires.add((CrySLPredicate) con);
			}
		}
		return requires;
	}
	
	public String toString() {
		StringBuilder outputSB = new StringBuilder();
		
		outputSB.append(this.className);
		
		outputSB.append("\nforbiddenMethods:");
		for (CrySLForbiddenMethod forbMethSig : this.forbiddenMethods) {
			
			outputSB.append(forbMethSig);
			outputSB.append(",");
		}
		
		outputSB.append("\nUsage Pattern:");
		outputSB.append(this.usagePattern);
		
		outputSB.append("\nConstraints:");
		for (ISLConstraint constraint : this.constraints) {
			outputSB.append(constraint);
			outputSB.append(",");
		}

		if (this.predicates != null) {
			outputSB.append("\nPredicates:");
			for (CrySLPredicate predicate : this.predicates) {
				outputSB.append(predicate);
				outputSB.append(",");
			}
		}
		
		return outputSB.toString();
	}
	
	public static Collection<String> toSubSignatures(Collection<SootMethod> methods) {
		Set<String> subSignatures = Sets.newHashSet();
		for(SootMethod m : methods){
			subSignatures.add(m.getName());
		}
		return subSignatures;
	}
	
}
