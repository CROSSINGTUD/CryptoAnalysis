package crypto.rules;

import boomerang.scene.ControlFlowGraph;
import crypto.interfaces.ISLConstraint;

public abstract class CrySLLiteral implements ISLConstraint {

	private static final long serialVersionUID = 1L;
	private ControlFlowGraph.Edge location;

	public void setLocation(ControlFlowGraph.Edge location) {
		this.location = location;
	}
	
	public ControlFlowGraph.Edge getLocation() {
		return location;
	}

}
