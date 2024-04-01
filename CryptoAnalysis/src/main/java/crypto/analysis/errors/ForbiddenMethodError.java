package crypto.analysis.errors;

import boomerang.scene.DeclaredMethod;
import boomerang.scene.Statement;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import crypto.rules.CrySLMethod;
import crypto.rules.CrySLRule;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ForbiddenMethodError extends AbstractError {

	private final DeclaredMethod calledMethod;
	private final Collection<CrySLMethod> alternatives;
	private final Set<String> alternativesSet = Sets.newHashSet();

	public ForbiddenMethodError(Statement errorLocation, CrySLRule rule, DeclaredMethod calledMethod) {
		this(errorLocation, rule, calledMethod, new HashSet<>());
	}

	public ForbiddenMethodError(Statement errorLocation, CrySLRule rule, DeclaredMethod calledMethod,
								Collection<CrySLMethod> alternatives) {
		super(errorLocation, rule);
		this.calledMethod = calledMethod;
		this.alternatives = alternatives;
		
		for (CrySLMethod method : alternatives) {
			this.alternativesSet.add(method.getName());
		}	
	}

	public Collection<CrySLMethod> getAlternatives() {
		return alternatives;
	}

	public void accept(ErrorVisitor visitor) {
		visitor.visit(this);
	}

	public DeclaredMethod getCalledMethod() {
		return calledMethod;
	}

	@Override
	public String toErrorMarkerString() {
		final StringBuilder msg = new StringBuilder();
		msg.append("Detected call to forbidden method ");
		msg.append(getCalledMethod().getSubSignature());
		msg.append(" of class ");
		msg.append(getCalledMethod().getDeclaringClass());
		if (!getAlternatives().isEmpty()) {
			msg.append(". Instead, call method ");
			Collection<CrySLMethod> subSignatures = getAlternatives();
			msg.append(Joiner.on(", ").join(subSignatures));
			msg.append(".");
		}
		return msg.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((alternativesSet == null) ? 0 : alternativesSet.hashCode());
		result = prime * result + ((calledMethod == null) ? 0 : calledMethod.getName().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ForbiddenMethodError other = (ForbiddenMethodError) obj;
		if (alternativesSet == null) {
			if (other.alternativesSet != null)
				return false;
		} else if (!alternativesSet.equals(other.alternativesSet))
			return false;
		if (calledMethod == null) {
			if (other.calledMethod != null)
				return false;
		} else if (!calledMethod.getName().equals(other.calledMethod.getName()))
			return false;
		return true;
	}
}
