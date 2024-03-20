package crypto.analysis.errors;

import boomerang.scene.Statement;
import boomerang.scene.WrappedClass;
import crypto.rules.CrySLRule;

public class UncaughtExceptionError extends AbstractError {

	private final WrappedClass exception;

	public UncaughtExceptionError(Statement errorStmt, CrySLRule rule, WrappedClass exception) {
		super(errorStmt, rule);
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

