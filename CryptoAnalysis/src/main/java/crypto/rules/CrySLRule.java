package crypto.rules;

import crypto.interfaces.ISLConstraint;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

public class CrySLRule {

	private final String className;
	
	private final List<Entry<String, String>> objects;
	
	private final List<CrySLForbiddenMethod> forbiddenMethods;

	private final Collection<CrySLMethod> events;
	
	private final StateMachineGraph usagePattern;
	
	private final List<ISLConstraint> constraints;
	
	private final List<CrySLPredicate> predicates;
	
	private final List<CrySLPredicate> negatedPredicates;
	
	public CrySLRule(String className, List<Entry<String, String>> objects, List<CrySLForbiddenMethod> forbiddenMethods, Collection<CrySLMethod> events, StateMachineGraph usagePattern, List<ISLConstraint> constraints, List<CrySLPredicate> predicates, List<CrySLPredicate> negatedPredicates) {
		this.className = className;
		this.objects = objects;
		this.forbiddenMethods =forbiddenMethods;
		this.events = events;
		this.usagePattern = usagePattern;
		this.constraints = constraints;
		this.predicates = predicates;
		this.negatedPredicates = negatedPredicates;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CrySLRule) {
			return ((CrySLRule) obj).getClassName().equals(className);
		} 
		return false;
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
	 * @return the events
	 */
	public Collection<CrySLMethod> getEvents() {
		return events;
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
	 * @return the negated predicates
	 */
	public List<CrySLPredicate> getNegatedPredicates() {
		return negatedPredicates;
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

	@Override
	public String toString() {
		StringBuilder outputSB = new StringBuilder();
		
		outputSB.append(this.className);
		
		outputSB.append("\nforbiddenMethods:");
		for (CrySLForbiddenMethod forMethSig : this.forbiddenMethods) {
			outputSB.append(forMethSig);
			outputSB.append(", ");
		}

		outputSB.append("\nEvents:");
		for (CrySLMethod method : events) {
			outputSB.append(method);
			outputSB.append(", ");
		}
		
		outputSB.append("\nUsage Pattern:");
		outputSB.append(this.usagePattern);
		
		outputSB.append("\nConstraints:");
		for (ISLConstraint constraint : this.constraints) {
			outputSB.append(constraint);
			outputSB.append(", ");
		}

		if (this.predicates != null) {
			outputSB.append("\nPredicates:");
			for (CrySLPredicate predicate : this.predicates) {
				outputSB.append(predicate);
				outputSB.append(", ");
			}
		}
		
		if (this.negatedPredicates != null) {
			outputSB.append("\nNegated predicates:");
			for (CrySLPredicate predicate : this.negatedPredicates) {
				outputSB.append(predicate);
				outputSB.append(", ");
			}
		}
		
		return outputSB.toString();
	}

}
