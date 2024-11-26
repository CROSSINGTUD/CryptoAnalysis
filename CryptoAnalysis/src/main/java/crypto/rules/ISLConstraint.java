package crypto.rules;

import java.util.List;

public interface ISLConstraint extends ICrySLPredicateParameter {

    List<String> getInvolvedVarNames();

}
