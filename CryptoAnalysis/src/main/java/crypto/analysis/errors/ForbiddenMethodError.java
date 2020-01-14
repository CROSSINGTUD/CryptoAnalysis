package crypto.analysis.errors;

import java.util.Collection;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import boomerang.jimple.Statement;
import crypto.rules.CrySLRule;
import soot.SootMethod;

public class ForbiddenMethodError extends AbstractError {

	private Collection<SootMethod> alternatives;
	private SootMethod calledMethod;
	private Set<String> alternativesSet = Sets.newHashSet();

	public ForbiddenMethodError(Statement errorLocation, CrySLRule rule, SootMethod calledMethod,
			Collection<SootMethod> collection) {
		super(errorLocation, rule);
		this.calledMethod = calledMethod;
		this.alternatives = collection;
		
		for (SootMethod method : alternatives) {
			this.alternativesSet.add(method.getSignature());
		}	
	}

	public Collection<SootMethod> getAlternatives() {
		return alternatives;
	}

	public void accept(ErrorVisitor visitor) {
		visitor.visit(this);
	}

	public SootMethod getCalledMethod() {
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
			Collection<SootMethod> subSignatures = getAlternatives();
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
		result = prime * result + ((calledMethod == null) ? 0 : calledMethod.getSignature().hashCode());
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
		} else if (!calledMethod.getSignature().equals(other.calledMethod.getSignature()))
			return false;
		return true;
	}


	

}
