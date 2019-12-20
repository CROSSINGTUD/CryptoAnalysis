package crypto.analysis.errors;

import boomerang.jimple.Statement;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CrySLRule;

public class ImpreciseValueExtractionError extends AbstractError {

	private ISLConstraint violatedConstraint;

	public ImpreciseValueExtractionError(ISLConstraint violatedCons, Statement errorLocation, CrySLRule rule) {
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((violatedConstraint == null) ? 0 : violatedConstraint.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImpreciseValueExtractionError other = (ImpreciseValueExtractionError) obj;
		if (violatedConstraint == null) {
			if (other.violatedConstraint != null)
				return false;
		} else if (!violatedConstraint.equals(other.violatedConstraint))
			return false;
		return true;
	}

}
