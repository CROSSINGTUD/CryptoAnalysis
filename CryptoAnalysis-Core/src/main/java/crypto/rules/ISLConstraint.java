package crypto.rules;

import boomerang.scene.Statement;

import java.util.List;
import java.util.Set;

public interface ISLConstraint extends ICrySLPredicateParameter {

	List<String> getInvolvedVarNames();

	Statement getLocation();

}
