package crypto.predicates;

import crypto.analysis.IAnalysisSeed;
import java.util.Collection;

public interface IPredicateCheckListener {

    void beforePredicateChecks(Collection<IAnalysisSeed> seeds);

    void propagatePredicates();

    void afterPredicateChecks();
}
