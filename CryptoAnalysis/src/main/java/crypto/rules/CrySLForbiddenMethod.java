package crypto.rules;

import java.util.Collection;

public class CrySLForbiddenMethod {

	private final CrySLMethod method;
	private final Collection<CrySLMethod> alternatives;

	public CrySLForbiddenMethod(CrySLMethod method, Collection<CrySLMethod> alternatives) {
		this.method = method;
		this.alternatives = alternatives;
	}

	public CrySLMethod getMethod() {
		return method;
	}
	
	public Collection<CrySLMethod> getAlternatives() {
		return alternatives;
	}

	@Override
	public String toString() {
		final StringBuilder forbiddenMethod = new StringBuilder();
		forbiddenMethod.append(method.toString());
		if (!alternatives.isEmpty()) {
			forbiddenMethod.append(" Alternatives: ");
		}
		
		for (CrySLMethod meth : alternatives) {
			forbiddenMethod.append(meth.toString());
		}
		
		return forbiddenMethod.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof CrySLForbiddenMethod)) {
			return false;
		}

		CrySLForbiddenMethod other = (CrySLForbiddenMethod) obj;
		if (!method.equals(other.getMethod())) {
			return false;
		}

        return alternatives.equals(other.getAlternatives());
    }
	
}