package crypto.analysis.errors;

import java.util.Collection;

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
}
