package crypto.rules;

import boomerang.scene.Statement;

import java.util.Set;

public interface ISLConstraint extends ICrySLPredicateParameter {

	Set<String> getInvolvedVarNames();

	Statement getLocation();

}
