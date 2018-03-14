package crypto.interfaces;

import java.util.Set;

import boomerang.jimple.Statement;

public interface ISLConstraint extends java.io.Serializable, ICryptSLPredicateParameter {

	public Set<String> getInvolvedVarNames(); 

	public void setLocation(Statement location);

	public Statement getLocation();

}
