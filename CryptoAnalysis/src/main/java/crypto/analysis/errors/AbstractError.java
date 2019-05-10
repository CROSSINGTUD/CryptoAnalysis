package crypto.analysis.errors;

import boomerang.jimple.Statement;
import crypto.rules.CryptSLRule;

public abstract class AbstractError implements IError{
	private Statement errorLocation;
	private CryptSLRule rule;

	public AbstractError(Statement errorLocation, CryptSLRule rule) {
		this.errorLocation = errorLocation;
		this.rule = rule;
	}

	public Statement getErrorLocation() {
		return errorLocation;
	}

	public CryptSLRule getRule() {
		return rule;
	}
	public abstract String toErrorMarkerString();

	public String toString() {
		return toErrorMarkerString();
	}
}
