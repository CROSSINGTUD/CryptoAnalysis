package crypto.rules;

import java.io.Serializable;

public class CryptSLForbiddenMethod implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CryptSLMethod meth;
	private Boolean silent;// = false;
	
	public CryptSLForbiddenMethod(CryptSLMethod method, Boolean silent) {
		this.meth = method;
		this.silent = silent;
	}
	
	public Boolean getSilent() {
		return silent;
	}
	
	public CryptSLMethod getMethod() {
		return meth;
	}
	
	public String toString() {
		return meth.toString() + "( silent: " + silent + ")";
	}
	
}