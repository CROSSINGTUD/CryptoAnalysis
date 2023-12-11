package crypto.rules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CrySLForbiddenMethod implements Serializable{

	private static final long serialVersionUID = 1L;
	private CrySLMethod meth;
	private Boolean silent;// = false;
	private List<CrySLMethod> alternate;
	
	public CrySLForbiddenMethod(CrySLMethod method, Boolean silent) {
		this(method, silent, new ArrayList<CrySLMethod>());
	}
	
	public CrySLForbiddenMethod(CrySLMethod method, Boolean silent, List<CrySLMethod> alternatives) {
		this.meth = method;
		this.silent = silent;
		this.alternate = alternatives;
	}

	public CrySLMethod getMethod() {
		return meth;
	}
	
	public Boolean getSilent() {
		return silent;
	}
	
	public List<CrySLMethod> getAlternatives() {
		return alternate;
	}
	
	public String toString() {
		final StringBuilder forbMethod = new StringBuilder();
		forbMethod.append(meth.toString());
		forbMethod.append("( silent: " + silent + ")");
		if (!alternate.isEmpty()) {
			forbMethod.append(" Alternatives: ");
		}
		
		for (CrySLMethod meth : alternate) {
			forbMethod.append(meth.toString());
		}
		
		return forbMethod.toString();
	}
	
}