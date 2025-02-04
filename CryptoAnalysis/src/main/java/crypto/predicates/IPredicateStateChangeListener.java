package crypto.predicates;

import crypto.analysis.IAnalysisSeed;

public interface IPredicateStateChangeListener {

    void onPredicateStateChange(IAnalysisSeed seed);
}
