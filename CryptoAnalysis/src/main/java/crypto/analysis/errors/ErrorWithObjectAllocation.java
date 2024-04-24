package crypto.analysis.errors;

import boomerang.scene.Statement;
import crypto.analysis.IAnalysisSeed;
import crypto.rules.CrySLRule;

public abstract class ErrorWithObjectAllocation extends AbstractError {
	private final IAnalysisSeed objectAllocationLocation;

	public ErrorWithObjectAllocation(Statement errorStmt, CrySLRule rule, IAnalysisSeed objectAllocationLocation) {
		super(errorStmt, rule);
		this.objectAllocationLocation = objectAllocationLocation;
	}

	public IAnalysisSeed getObjectLocation(){
		return objectAllocationLocation;
	}

	protected String getObjectType() {
		if (this.objectAllocationLocation.getForwardQuery().asNode().fact() != null && !this.objectAllocationLocation.getForwardQuery().asNode().fact().isNull())
			return " on object of type " + this.objectAllocationLocation.getForwardQuery().asNode().fact().getType();
		return "";
	}

}
