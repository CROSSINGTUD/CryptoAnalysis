package typestate.interfaces;

import java.util.Set;

public interface ISLConstraint extends java.io.Serializable, ICryptSLPredicateParameter {

	public Set<String> getInvolvedVarNames(); 
}
