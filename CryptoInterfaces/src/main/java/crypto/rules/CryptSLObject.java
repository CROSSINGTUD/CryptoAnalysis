package crypto.rules;

import java.io.Serializable;

import typestate.interfaces.ICryptSLPredicateParameter;

public class CryptSLObject implements Serializable, ICryptSLPredicateParameter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String varName;
	private CryptSLSplitter splitter;
	
	public CryptSLObject(String name) {
		varName = name;
		splitter = null;
	}
	
	public CryptSLObject(String name, CryptSLSplitter part) {
		varName = name;
		splitter = part;
	}
	
	/**
	 * @return the varName
	 */
	public String getVarName() {
		return varName;
	}

	/**
	 * @return the splitter
	 */
	public CryptSLSplitter getSplitter() {
		return splitter;
	}
	
	public String toString() {
		return varName + ((splitter != null) ? splitter.toString() : "");
	}

	@Override
	public String getName() {
		return varName;
	}
	
}
