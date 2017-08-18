package crypto.analysis;

import soot.Unit;
import typestate.interfaces.ISLConstraint;

public interface ConstaintReporter {

	public void constraintViolated(ISLConstraint con);
	
	void callToForbiddenMethod(ClassSpecification classSpecification, Unit callSite);

}
