package crypto.rules;

import java.io.Serializable;
import java.util.List;
import java.util.Map.Entry;

public class CryptSLMethod implements Serializable, ICryptSLPredicateParameter {
	
	private String methodName;
	private List<Entry<String, String>> parameters; 
	private List<Boolean> backward;
	
	public CryptSLMethod(String methName, List<Entry<String, String>> pars, List<Boolean> backw) {
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
		return getName();
	}

	@Override
	public String getName() {
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
			if (backward != null) {
				stmntBuilder.append(" (");
				stmntBuilder.append(backward.get(parameters.indexOf(par)));
				stmntBuilder.append("),");
			}
		}
		
		stmntBuilder.append(");");
		return stmntBuilder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((backward == null) ? 0 : backward.hashCode());
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CryptSLMethod)) {
			return false;
		}
		CryptSLMethod other = (CryptSLMethod) obj;
		return this.getMethodName().equals(other.getMethodName()) && parameters.equals(other.parameters);
	}
}