package crypto.rules;

import java.util.List;

import typestate.interfaces.ISLConstraint;

public class CryptSLRule implements java.io.Serializable {

	private final String className;
	
	private List<String> forbiddenMethods;
	
	private StateMachineGraph usagePattern;
	
	private List<ISLConstraint> constraints;
	
	private List<CryptSLPredicate> predicates;
	
	public CryptSLRule(String _className, List<String> _forbiddenMethods, StateMachineGraph _usagePattern, List<ISLConstraint> _constraints, List<CryptSLPredicate> _predicates) {
		className = _className;
		forbiddenMethods = _forbiddenMethods;
		usagePattern = _usagePattern;
		constraints = _constraints;
		predicates = _predicates;
		System.out.println(this);
	}
	
	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @return the forbiddenMethods
	 */
	public List<String> getForbiddenMethods() {
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

	public String toString() {
		StringBuilder outputSB = new StringBuilder();
		
		outputSB.append(this.className);
		
		outputSB.append("\nforbiddenMethods:");
		for (String forbMethSig : this.forbiddenMethods) {
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

		outputSB.append("\nPredicates:");
		for (CryptSLPredicate predicate : this.predicates) {
			outputSB.append(predicate);
			outputSB.append(",");
		}
		
		return outputSB.toString();
	}
	
}
