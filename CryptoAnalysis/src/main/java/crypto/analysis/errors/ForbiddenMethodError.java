package crypto.analysis.errors;

import java.util.Collection;

import com.google.common.base.Joiner;

import boomerang.jimple.Statement;
import crypto.rules.CryptSLRule;
import soot.SootMethod;

public class ForbiddenMethodError extends AbstractError{

	private Collection<SootMethod> alternatives;
	private SootMethod calledMethod;

	public ForbiddenMethodError(Statement errorLocation, CryptSLRule rule, SootMethod calledMethod, Collection<SootMethod> collection) {
		super(errorLocation, rule);
		this.calledMethod = calledMethod;
		this.alternatives = collection;
	}

	public Collection<SootMethod> getAlternatives() {
		return alternatives;
	}

	public void accept(ErrorVisitor visitor){
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
		result = prime * result + ((alternatives == null) ? 0 : alternatives.hashCode());
		result = prime * result + ((calledMethod == null) ? 0 : calledMethod.hashCode());
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
		if (alternatives == null) {
			if (other.alternatives != null)
				return false;
		} else if (!alternatives.equals(other.alternatives))
			return false;
		if (calledMethod == null) {
			if (other.calledMethod != null)
				return false;
		} else if (!calledMethod.equals(other.calledMethod))
			return false;
		return true;
	}

}
