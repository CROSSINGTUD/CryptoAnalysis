package crypto.rules;

import java.util.ArrayList;
import java.util.List;

import typestate.interfaces.ICryptSLPredicateParameter;

public class CryptSLPredicate extends CryptSLLiteral implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private final ICryptSLPredicateParameter baseObject;
	private final String predName;
	private final List<ICryptSLPredicateParameter> parameters;
	private final boolean negated;
	
	public CryptSLPredicate(ICryptSLPredicateParameter baseObject, String name, List<ICryptSLPredicateParameter> variables, Boolean not) {
		this.baseObject = baseObject;
		this.predName = name;
		this.parameters = variables;
		this.negated = not;
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
	 * @return the baseObject
	 */
	public ICryptSLPredicateParameter getBaseObject() {
		return baseObject;
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
		for (ICryptSLPredicateParameter var : parameters) {
			if (!("_".equals(var.getName()) || "this".equals(var.getName()))) {
				varNames.add(var.getName());
			}
		}
		return varNames;
	}
	
}
