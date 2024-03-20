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
		if(this.objectAllocationLocation.asNode().fact() != null && !this.objectAllocationLocation.asNode().fact().isNull())
			return " on object of type " + this.objectAllocationLocation.asNode().fact().getType();
		return "";
	}

}
