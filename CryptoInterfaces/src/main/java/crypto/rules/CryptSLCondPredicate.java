package crypto.rules;

import java.util.List;
import java.util.Set;

import typestate.interfaces.ICryptSLPredicateParameter;

public class CryptSLCondPredicate extends CryptSLPredicate {
	
	private final Set<StateNode> conditionalNodes;
	
	public CryptSLCondPredicate(String name, List<ICryptSLPredicateParameter> variables, Boolean not, Set<StateNode> label) {
		super(name, variables, not);
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
