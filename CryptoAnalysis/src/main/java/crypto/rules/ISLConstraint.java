package crypto.rules;

import boomerang.scene.Statement;
import java.util.List;

public interface ISLConstraint extends ICrySLPredicateParameter {

    List<String> getInvolvedVarNames();

    Statement getLocation();
}
