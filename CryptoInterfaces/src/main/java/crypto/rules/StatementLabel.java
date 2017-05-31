package crypto.rules;

import java.util.List;

public class StatementLabel {
	
	private String methodName;
	private List<String> parameters; 
	private List<Boolean> backward;
	
	public StatementLabel(String methName, List<String> pars, List<Boolean> backw) {
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
	public List<String> getParameters() {
		return parameters;
	}
	
	/**
	 * @return the backward
	 */
	public List<Boolean> getBackward() {
		return backward;
	}
	
}