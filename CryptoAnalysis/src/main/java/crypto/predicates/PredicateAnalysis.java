package crypto.predicates;

import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.AbstractRequiredPredicateError;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class PredicateAnalysis {

    private final Queue<IAnalysisSeed> workQueue;

    public PredicateAnalysis() {
        this.workQueue = new LinkedList<>();
    }

    public void checkPredicates(Collection<IAnalysisSeed> seeds) {
        for (IPredicateCheckListener seed : seeds) {
            seed.beforePredicateChecks(seeds);
        }

        propagationPhase(seeds);

        for (IPredicateCheckListener seed : seeds) {
            seed.afterPredicateChecks();
        }

        connectSubsequentErrors(seeds);
    }

    private void propagationPhase(Collection<IAnalysisSeed> seeds) {
        for (IAnalysisSeed seed : seeds) {
            seed.resetPredicateStateChangeListener();
            seed.addPredicateStateChangeListener(
                    updatedSeed -> {
                        if (!workQueue.contains(updatedSeed)) {
                            workQueue.add(updatedSeed);
                        }
                    });
        }

        workQueue.clear();
        workQueue.addAll(seeds);

        while (!workQueue.isEmpty()) {
            IAnalysisSeed seed = workQueue.element();

            seed.propagatePredicates();

            workQueue.remove();
        }
    }

    private void connectSubsequentErrors(Collection<IAnalysisSeed> seeds) {
        for (IAnalysisSeed seed : seeds) {
            for (AbstractError error : seed.getErrors()) {
                if (!(error instanceof AbstractRequiredPredicateError reqPredError)) {
                    continue;
                }

                for (UnEnsuredPredicate unEnsuredPredicate : reqPredError.getHiddenPredicates()) {
                    Collection<AbstractError> precedingErrors =
                            unEnsuredPredicate.getPrecedingErrors();

                    precedingErrors.forEach(error::addPrecedingError);
                    precedingErrors.forEach(e -> e.addSubsequentError(error));
                }
            }
        }
    }
}
