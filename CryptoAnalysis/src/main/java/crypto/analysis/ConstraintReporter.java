package crypto.analysis;

import java.util.Collection;

import boomerang.scene.ControlFlowGraph;
import crypto.interfaces.ISLConstraint;
import soot.SootMethod;

public interface ConstraintReporter {

	public void constraintViolated(ISLConstraint con, ControlFlowGraph.Edge unit);
	
	void callToForbiddenMethod(ClassSpecification classSpecification, ControlFlowGraph.Edge callSite, SootMethod foundCall, Collection<SootMethod> convert);

	public void unevaluableConstraint(ISLConstraint con, ControlFlowGraph.Edge unit);
}
