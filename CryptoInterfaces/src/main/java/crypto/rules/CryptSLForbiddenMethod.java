package crypto.rules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CryptSLForbiddenMethod implements Serializable{

	private static final long serialVersionUID = 1L;
	private CryptSLMethod meth;
	private Boolean silent;// = false;
	private List<CryptSLMethod> alternate;
	
	public CryptSLForbiddenMethod(CryptSLMethod method, Boolean silent) {
		this(method, silent, new ArrayList<CryptSLMethod>());
	}
	
	public CryptSLForbiddenMethod(CryptSLMethod method, Boolean silent, List<CryptSLMethod> alternatives) {
		this.meth = method;
		this.silent = silent;
		this.alternate = alternatives;
	}

	public CryptSLMethod getMethod() {
		return meth;
	}
	
	public Boolean getSilent() {
		return silent;
	}
	
	public List<CryptSLMethod> getAlternatives() {
		return alternate;
	}
	
	public String toString() {
		return meth.toString() + "( silent: " + silent + ")";
	}
	
}