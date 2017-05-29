package crypto.DSL;

import java.util.List;

public class CryptSLPredicate extends CryptSLLiteral {

	private String predName;
	private List<String> parameters;
	private Boolean negated;
	
	public CryptSLPredicate(String name, List<String> variables, Boolean not) {
		predName = name;
		parameters = variables;
		negated = not;
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
	
}
