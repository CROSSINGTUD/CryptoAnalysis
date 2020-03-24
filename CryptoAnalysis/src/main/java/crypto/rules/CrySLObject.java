package crypto.rules;

import java.io.Serializable;

import crypto.interfaces.ICrySLPredicateParameter;


public class CrySLObject implements Serializable, ICrySLPredicateParameter {

	private static final long serialVersionUID = 1L;
	private String varName;
	private String javaType;
	private CrySLSplitter splitter;
	
	public CrySLObject(String name, String type) {
		this(name, type, null);
	}
	
	public CrySLObject(String name, String type, CrySLSplitter part) {
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
	public CrySLSplitter getSplitter() {
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
