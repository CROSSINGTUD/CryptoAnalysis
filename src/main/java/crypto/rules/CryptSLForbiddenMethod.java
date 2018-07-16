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
		final StringBuilder forbMethod = new StringBuilder();
		forbMethod.append(meth.toString());
		forbMethod.append("( silent: " + silent + ")");
		if (!alternate.isEmpty()) {
			forbMethod.append(" Alternatives: ");
		}
		
		for (CryptSLMethod meth : alternate) {
			forbMethod.append(meth.toString());
		}
		
		return forbMethod.toString();
	}
	
}