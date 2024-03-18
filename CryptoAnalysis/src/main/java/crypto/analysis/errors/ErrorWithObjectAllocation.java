package crypto.analysis.errors;

import java.util.Set;

import boomerang.scene.ControlFlowGraph;
import boomerang.scene.Val;
import crypto.analysis.IAnalysisSeed;
import crypto.rules.CrySLRule;
import sync.pds.solver.nodes.Node;

public abstract class ErrorWithObjectAllocation extends AbstractError{
	private final IAnalysisSeed objectAllocationLocation;

	public ErrorWithObjectAllocation(ControlFlowGraph.Edge errorLocation, CrySLRule rule, IAnalysisSeed objectAllocationLocation) {
		super(errorLocation, rule);
		this.objectAllocationLocation = objectAllocationLocation;
	}

	public IAnalysisSeed getObjectLocation(){
		return objectAllocationLocation;
	}

	protected String getObjectType() {
		if(this.objectAllocationLocation.asNode().fact() != null && this.objectAllocationLocation.asNode().fact().value() != null)
			return " on object of type " + this.objectAllocationLocation.asNode().fact().value().getType();
		return "";
	}
	
	public Set<Node<ControlFlowGraph.Edge, Val>> getDataFlowPath(){
		return objectAllocationLocation.getDataFlowPath();
	}
}
