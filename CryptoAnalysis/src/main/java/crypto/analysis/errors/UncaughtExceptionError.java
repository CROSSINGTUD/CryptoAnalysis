package crypto.analysis.errors;

import boomerang.jimple.Statement;
import crypto.rules.CrySLRule;
import soot.SootClass;

public class UncaughtExceptionError extends AbstractError {

	private final SootClass exception;

	public UncaughtExceptionError(Statement errorLocation, CrySLRule rule, SootClass exception) {
		super(errorLocation, rule);
		this.exception = exception;
	}

	@Override
	public void accept(ErrorVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toErrorMarkerString() {
		return String.format("Uncaught exception `%s`", exception.getName());
	}

}

