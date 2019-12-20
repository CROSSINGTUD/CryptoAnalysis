package crypto.rules;

import java.util.List;
import java.util.Set;

import crypto.interfaces.ICrySLPredicateParameter;

public class CrySLCondPredicate extends CrySLPredicate {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Set<StateNode> conditionalNodes;
	
	public CrySLCondPredicate(ICrySLPredicateParameter baseObj, String name, List<ICrySLPredicateParameter> variables, Boolean not, Set<StateNode> label) {
		super(baseObj, name, variables, not);
		conditionalNodes = label;
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
