package crypto.analysis.errors;

import java.util.Collection;

import boomerang.jimple.Statement;
import crypto.rules.CryptSLRule;
import soot.SootMethod;

public class TypestateError extends AbstractError{

	private Collection<SootMethod> expectedMethodCalls;

	public TypestateError(Statement stmt, CryptSLRule rule, Collection<SootMethod> expectedMethodCalls) {
		super(stmt, rule);
		this.expectedMethodCalls = expectedMethodCalls;
	}

	public Collection<SootMethod> getExpectedMethodCalls() {
		return expectedMethodCalls;
	}

	public void accept(ErrorVisitor visitor){
		visitor.visit(this);
	}
}
