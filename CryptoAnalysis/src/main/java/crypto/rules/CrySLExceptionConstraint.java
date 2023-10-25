package crypto.rules;

import java.util.Collections;
import java.util.Set;

import boomerang.jimple.Statement;
import crypto.interfaces.ISLConstraint;

/**
 * Constraint expressing, that a {@link CrySLMethod} throws an
 * {@link CrySLException}, that must be caught.
 * */
public class CrySLExceptionConstraint implements ISLConstraint {

	/**
	 * The Method throwing the Exception.
	 * */
	private final CrySLMethod method;

	/**
	 * The Exception thrown by the Method.
	 * */
	private final CrySLException exception;

	private Statement location = null;

	/**
	 * Construct the {@link CrySLExceptionConstraint} given the method and the
	 * exception thrown thereby.
	 *
	 * @param method Method that throws the Exception.
	 * @param exception Exception that thrown by the Method.
	 */
	public CrySLExceptionConstraint(CrySLMethod method, CrySLException exception) {
		this.method = method;
		this.exception = exception;
	}

	/**
	 * Returns the Method throwing the Exception.
	 *
	 * @return The Method throwing the Exception.
	 * */
	public CrySLMethod getMethod() {
		return method;
	}

	/**
	 * Returns the Exception thrown by the Exception.
	 *
	 * @return The Exception thrown by the Exception.
	 * */
	public CrySLException getException() {
		return exception;
	}

	public String toString() {
		return String.format("%s(%s, %s)", this.getClass().getName(), getMethod(), getException());
	}

	@Override
	public void setLocation(Statement location) {
		this.location = location;
	}

	@Override
	public Statement getLocation() {
		return this.location;
	}

	@Override
	public Set<String> getInvolvedVarNames() {
		return Collections.emptySet();
	}

	@Override
	public String getName() {
		return toString();
	}

}
