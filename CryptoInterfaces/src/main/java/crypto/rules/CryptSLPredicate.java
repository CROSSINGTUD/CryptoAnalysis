package crypto.rules;

import java.util.ArrayList;
import java.util.List;

import typestate.interfaces.ICryptSLPredicateParameter;

public class CryptSLPredicate extends CryptSLLiteral implements java.io.Serializable {

	private final String predName;
	private final List<ICryptSLPredicateParameter> parameters;
	private final boolean negated;
	private final List<StateNode> conditionalNodes;
	
	public CryptSLPredicate(String name, List<ICryptSLPredicateParameter> variables, Boolean not) {
		predName = name;
		parameters = variables;
		negated = not;
		conditionalNodes = new ArrayList<StateNode>();
	}
	
	public CryptSLPredicate(String name, List<ICryptSLPredicateParameter> variables, Boolean not, List<StateNode> label) {
		predName = name;
		parameters = variables;
		negated = not;
		conditionalNodes = label;
	}
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof CryptSLPredicate)) {
			return false;
		}
		CryptSLPredicate other = (CryptSLPredicate) obj;
		return other.getPredName().equals(this.predName);// && (new HashSet<String>(other.getParameters())).equals(new HashSet<String>(this.getParameters()));
	}

	/**
	 * @return the predName
	 */
	public String getPredName() {
		return predName;
	}

	/**
	 * @return the parameters
	 */
	public List<ICryptSLPredicateParameter> getParameters() {
		return parameters;
	}

	/**
	 * @return the negated
	 */
	public Boolean isNegated() {
		return negated;
	}
	
	/**
	 * @return the conditionalMethods
	 */
	public List<StateNode> getConditionalMethods() {
		return conditionalNodes;
	}
	
	public String toString() {
		StringBuilder predSB = new StringBuilder();
		predSB.append("P:");
		if (negated) {
			predSB.append("!");
		}
		predSB.append(predName);
		predSB.append("(");
		
		for (ICryptSLPredicateParameter parameter : parameters) {
			predSB.append(parameter);
			predSB.append(",");
		}
		predSB.append(")");
		
		
		return predSB.toString();
	}

	@Override
	public List<String> getInvolvedVarNames() {
		List<String> varNames = new ArrayList<String>();
		boolean skipFirst = true;
		for (ICryptSLPredicateParameter var : parameters) {
			if (skipFirst) {
				skipFirst = false;
				continue;
			}
			if (!("_".equals(var.getName()) || "this".equals(var.getName()))) {
				varNames.add(var.getName());
			}
		}
		return varNames;
	}
	
}
