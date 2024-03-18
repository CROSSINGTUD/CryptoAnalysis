package crypto.interfaces;

import boomerang.scene.ControlFlowGraph;

import java.util.Set;

public interface ISLConstraint extends java.io.Serializable, ICrySLPredicateParameter {

	public Set<String> getInvolvedVarNames(); 

	public void setLocation(ControlFlowGraph.Edge location);

	public ControlFlowGraph.Edge getLocation();

}
