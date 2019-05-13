package crypto.analysis.errors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
		result = prime * result + ((alternatives == null) ? 0 :alternativesHashCode(alternatives));
		result = prime * result + ((calledMethod == null) ? 0 : calledMethod.getSignature().toString().hashCode());
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
		} else if (alternativesHashCode(alternatives) != (alternativesHashCode(other.alternatives)))
			return false;
		if (calledMethod == null) {
			if (other.calledMethod != null)
				return false;
		} else if (!calledMethod.getSignature().toString().equals(other.calledMethod.getSignature().toString()))
			return false;
		return true;
	}
	
	private int alternativesHashCode(Collection<SootMethod> alternatives) {
		final int prime = 31;
		int result = 1;
		
		List<SootMethod> alternativesList = new ArrayList<SootMethod>(alternatives);
		Collections.sort(alternativesList, new Comparator<SootMethod>() {
			public int compare(SootMethod s1, SootMethod s2) {
				return s1.getSignature().toString().compareToIgnoreCase(s2.getSignature().toString());
			}
		});

		for (SootMethod method : alternativesList) {
			result = prime * result + ((method == null) ? 0 : method.getSignature().toString().hashCode());
		}
		return result;
	}

}
