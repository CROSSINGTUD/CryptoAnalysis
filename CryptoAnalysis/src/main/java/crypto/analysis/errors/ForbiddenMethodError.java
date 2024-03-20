package crypto.analysis.errors;

import boomerang.scene.DeclaredMethod;
import boomerang.scene.Method;
import boomerang.scene.Statement;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import crypto.rules.CrySLRule;

import java.util.Collection;
import java.util.Set;

public class ForbiddenMethodError extends AbstractError {

	private Method calledMethod;
	private Collection<Method> alternatives;
	private Set<String> alternativesSet = Sets.newHashSet();

	public ForbiddenMethodError(Statement errorLocation, CrySLRule rule, Method calledMethod,
								Collection<Method> collection) {
		super(errorLocation, rule);
		this.calledMethod = calledMethod;
		this.alternatives = collection;
		
		for (Method method : alternatives) {
			this.alternativesSet.add(method.getName());
		}	
	}

	public Collection<Method> getAlternatives() {
		return alternatives;
	}

	public void accept(ErrorVisitor visitor) {
		visitor.visit(this);
	}

	public Method getCalledMethod() {
		return calledMethod;
	}

	@Override
	public String toErrorMarkerString() {
		final StringBuilder msg = new StringBuilder();
		msg.append("Detected call to forbidden method ");
		msg.append(getCalledMethod().getSubSignature());
		msg.append(" of class " + getCalledMethod().getDeclaringClass());
		if (!getAlternatives().isEmpty()) {
			msg.append(". Instead, call method ");
			Collection<Method> subSignatures = getAlternatives();
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
