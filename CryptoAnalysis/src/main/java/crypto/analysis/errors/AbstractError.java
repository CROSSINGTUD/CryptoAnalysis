package crypto.analysis.errors;

import boomerang.jimple.Statement;
import crypto.rules.CryptSLRule;

public abstract class AbstractError implements IError {
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

	@Override
	public int hashCode() {
		final String outerMethod = errorLocation.getMethod().getName();
		final String method = errorLocation.getUnit().get().getInvokeExpr().getMethod().toString();
		final int prime = 31;
		int result = 1;
		result = prime * result + ((outerMethod == null) ? 0 : outerMethod.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractError other = (AbstractError) obj;
		if (errorLocation.getMethod().getName() == null) {
			if (other.errorLocation.getMethod().getName() != null)
				return false;
		} else if (!errorLocation.getMethod().getName().equals(other.errorLocation.getMethod().getName()))
			return false;
		if (errorLocation.getUnit().get().getInvokeExpr().getMethod().toString() == null) {
			if (other.errorLocation.getUnit().get().getInvokeExpr().getMethod().toString() != null)
				return false;
		} else if (!errorLocation.getUnit().get().getInvokeExpr().getMethod().toString().equals(other.errorLocation.getUnit().get().getInvokeExpr().getMethod().toString()))
			return false;
		if (rule == null) {
			if (other.rule != null)
				return false;
		} else if (!rule.equals(other.rule))
			return false;
		return true;
	}
}
