package crypto.analysis.errors;

import boomerang.jimple.Statement;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLRule;

public class ImpreciseValueExtractionError extends AbstractError {

	private ISLConstraint violatedConstraint;

	public ImpreciseValueExtractionError(ISLConstraint violatedCons, Statement errorLocation, CryptSLRule rule) {
		super(errorLocation, rule);
		this.violatedConstraint = violatedCons;
	}

	@Override
	public void accept(ErrorVisitor visitor) {
		visitor.visit(this);
	}

	public ISLConstraint getViolatedConstraint() {
		return violatedConstraint;
	}

	@Override
	public String toErrorMarkerString() {
		StringBuilder msg = new StringBuilder("Constraint ");
		msg.append(violatedConstraint);
		msg.append(" could not be evaluted due to insufficient information.");
		return msg.toString();
	}

}
