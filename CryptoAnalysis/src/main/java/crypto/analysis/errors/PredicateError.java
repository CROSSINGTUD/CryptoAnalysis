package crypto.analysis.errors;

import boomerang.jimple.Statement;
import crypto.rules.CryptSLRule;

public class PredicateError extends AbstractError{

	public PredicateError(Statement stmt, CryptSLRule rule) {
		super(stmt, rule);
	}

	public void accept(ErrorVisitor visitor){
		visitor.visit(this);
	}
}
