package crypto.analysis.errors;

import boomerang.jimple.Statement;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLRule;

public class ImpreciseValueExtractionError extends AbstractError {

	ISLConstraint violatedConstraint;

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

}
