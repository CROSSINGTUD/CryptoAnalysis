package crypto.rules;

import java.util.ArrayList;
import java.util.List;

public class CryptSLPredicate extends CryptSLLiteral implements java.io.Serializable {

	private String predName;
	private List<String> parameters;
	private Boolean negated;
	
	public CryptSLPredicate(String name, List<String> variables, Boolean not) {
		predName = name;
		parameters = variables;
		negated = not;
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
	public List<String> getParameters() {
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
		
		for (String parameter : parameters) {
			predSB.append(parameter);
			predSB.append(",");
		}
		predSB.append(")");
		
		
		return predSB.toString();
	}

	@Override
	public List<String> getInvolvedVarNames() {
		List<String> varNames = new ArrayList<String>();
		for (String varName : parameters) {
			if (!("_".equals(varName) || "this".equals(varName))) {
				varNames.add(varName);
			}
		}
		return varNames;
	}
	
}
