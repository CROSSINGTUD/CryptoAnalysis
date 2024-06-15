package crypto.interfaces;

import boomerang.scene.Statement;

import java.util.Set;

public interface ISLConstraint extends java.io.Serializable, ICrySLPredicateParameter {

	public Set<String> getInvolvedVarNames(); 

	public void setLocation(Statement location);

	public Statement getLocation();

}
