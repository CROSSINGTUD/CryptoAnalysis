package crypto.rules;

import java.io.Serializable;
import java.util.List;
import java.util.Map.Entry;

public class StatementLabel implements Serializable{
	
	private String methodName;
	private List<Entry<String, String>> parameters; 
	private List<Boolean> backward;
	
	public StatementLabel(String methName, List<Entry<String, String>> pars, List<Boolean> backw) {
		methodName = methName;
		parameters = pars;
		backward = backw;
	}

	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * @return the parameters
	 */
	public List<Entry<String, String>> getParameters() {
		return parameters;
	}
	
	/**
	 * @return the backward
	 */
	public List<Boolean> getBackward() {
		return backward;
	}
	
	public String toString() {
		StringBuilder stmntBuilder = new StringBuilder();
		
		String returnValue = parameters.get(0).getKey();
		if (!"_".equals(returnValue)) {
			stmntBuilder.append(returnValue);
			stmntBuilder.append(" = ");
		}
		
		stmntBuilder.append(this.methodName);
		stmntBuilder.append("(");
		
		Boolean skipFirst = true;
		for (Entry<String, String> par: parameters) {
			if (skipFirst) {
				skipFirst = false;
				continue;
			}
			stmntBuilder.append(par.getValue());
			stmntBuilder.append(" ");
			stmntBuilder.append(par.getKey());
			stmntBuilder.append(",");
		}
		
		stmntBuilder.append(");");
		return stmntBuilder.toString();
	}
	
}