package crypto.analysis;

import boomerang.jimple.Statement;
import soot.Unit;
import typestate.interfaces.ISLConstraint;

public interface ConstraintReporter {

	public void constraintViolated(ISLConstraint con, Statement unit);
	
	void callToForbiddenMethod(ClassSpecification classSpecification, Statement callSite);

}
