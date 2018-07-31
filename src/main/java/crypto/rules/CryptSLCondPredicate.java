package crypto.rules;

import java.util.List;
import java.util.Set;

import crypto.interfaces.ICryptSLPredicateParameter;

public class CryptSLCondPredicate extends CryptSLPredicate {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Set<StateNode> conditionalNodes;
	
	public CryptSLCondPredicate(ICryptSLPredicateParameter baseObj, String name, List<ICryptSLPredicateParameter> variables, Boolean not, Set<StateNode> label) {
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
