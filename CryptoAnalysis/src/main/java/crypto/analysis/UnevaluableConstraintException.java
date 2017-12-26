package crypto.analysis;

import boomerang.jimple.Statement;
import crypto.rules.CryptSLPredicate;
import typestate.interfaces.ISLConstraint;

public class UnevaluableConstraintException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Statement atUnit;
	private ISLConstraint failedConstraint;

	public UnevaluableConstraintException(String message, CryptSLPredicate pred) {
		this(message, pred, null);
	}

	public UnevaluableConstraintException(String message, ISLConstraint cons, Statement location) {
		super(message);
		atUnit = location;
		failedConstraint = cons;
	}

	/**
	 * @return the failedConstraint
	 */
	public ISLConstraint getFailedConstraint() {
		return failedConstraint;
	}

	public Statement getUnit() {
		return atUnit;
	}
}