package crypto.rules;

import java.util.List;
import java.util.Set;
import crypto.interfaces.ICrySLPredicateParameter;
import crypto.interfaces.ISLConstraint;

public class CrySLCondPredicate extends CrySLPredicate {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Set<StateNode> conditionalNodes;
	
	public CrySLCondPredicate(ICrySLPredicateParameter baseObj, String name, List<ICrySLPredicateParameter> parameters, Boolean negated, Set<StateNode> nodes) {
		this(baseObj, name, parameters, negated, nodes, null);
	}
	
	public CrySLCondPredicate(ICrySLPredicateParameter baseObj, String name, List<ICrySLPredicateParameter> parameters, Boolean negated, Set<StateNode> nodes, ISLConstraint constraint) {
		super(baseObj, name, parameters, negated, constraint);
		this.conditionalNodes = nodes;
	}
	
	/**
	 * @return the conditionalMethods
	 */
	public Set<StateNode> getConditionalMethods() {
		return conditionalNodes;
	}
	
	public String toString() {
		return "COND" + super.toString();
	}

}
