package crypto.rules;

import java.io.Serializable;

import crypto.interfaces.ICryptSLPredicateParameter;


public class CryptSLObject implements Serializable, ICryptSLPredicateParameter {

	private static final long serialVersionUID = 1L;
	private String varName;
	private String javaType;
	private CryptSLSplitter splitter;
	
	public CryptSLObject(String name, String type) {
		this(name, type, null);
	}
	
	public CryptSLObject(String name, String type, CryptSLSplitter part) {
		varName = name;
		javaType = type;
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

	public String getJavaType() {
		return javaType;
	}
	
}
