package crypto.analysis;

import java.util.Collection;

import boomerang.jimple.Statement;
import soot.SootMethod;
import typestate.interfaces.ISLConstraint;

public interface ConstraintReporter {

	public void constraintViolated(ISLConstraint con, Statement unit);
	
	void callToForbiddenMethod(ClassSpecification classSpecification, Statement callSite, SootMethod foundCall, Collection<SootMethod> convert);

	public void unevaluableConstraint(ISLConstraint con, Statement unit);
}
