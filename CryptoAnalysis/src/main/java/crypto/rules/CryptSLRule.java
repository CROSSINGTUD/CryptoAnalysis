package crypto.rules;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import crypto.interfaces.ISLConstraint;

public class CryptSLRule implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String className;
	
	private final List<Entry<String, String>> objects;  
	
	private final List<CryptSLForbiddenMethod> forbiddenMethods;
	
	private final StateMachineGraph usagePattern;
	
	private final List<ISLConstraint> constraints;
	
	private final List<CryptSLPredicate> predicates;
	
	public CryptSLRule(String _className, List<Entry<String, String>> defObjects, List<CryptSLForbiddenMethod> _forbiddenMethods, StateMachineGraph _usagePattern, List<ISLConstraint> _constraints, List<CryptSLPredicate> _predicates) {
		className = _className;
		objects = defObjects;
		forbiddenMethods = _forbiddenMethods;
		usagePattern = _usagePattern;
		constraints = _constraints;
		predicates = _predicates;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CryptSLRule) {
			return ((CryptSLRule) obj).getClassName().equals(className);
		} 
		return false;
	}


	public boolean isLeafRule() {
		for (ISLConstraint con : constraints) {
			if (con instanceof CryptSLPredicate) {
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
	public List<CryptSLForbiddenMethod> getForbiddenMethods() {
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
	public List<CryptSLPredicate> getPredicates() {
		return predicates;
	}
	/**
	 * @return the constraints
	 */
	public List<CryptSLPredicate> getRequiredPredicates() {
		List<CryptSLPredicate> requires = new LinkedList<CryptSLPredicate>();
		for (ISLConstraint con : constraints) {
			if (con instanceof CryptSLPredicate) {
				requires.add((CryptSLPredicate) con);
			}
		}
		return requires;
	}
	
	public String toString() {
		StringBuilder outputSB = new StringBuilder();
		
		outputSB.append(this.className);
		
		outputSB.append("\nforbiddenMethods:");
		for (CryptSLForbiddenMethod forbMethSig : this.forbiddenMethods) {
			
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
			for (CryptSLPredicate predicate : this.predicates) {
				outputSB.append(predicate);
				outputSB.append(",");
			}
		}
		
		return outputSB.toString();
	}
	
}
