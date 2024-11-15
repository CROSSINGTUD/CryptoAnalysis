package crypto.listener;

import boomerang.results.BackwardBoomerangResults;
import boomerang.results.ForwardBoomerangResults;
import boomerang.scene.CallGraph;
import boomerang.scene.Statement;
import com.google.common.collect.Multimap;
import crypto.analysis.EnsuredCrySLPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.extractparameter.ExtractParameterQuery;
import crypto.rules.ISLConstraint;
import java.util.Collection;
import java.util.Map;
import typestate.TransitionFunction;
import wpds.impl.Weight;

public interface IResultsListener {

    void constructedCallGraph(CallGraph callGraph);

    void typestateAnalysisResults(
            IAnalysisSeed seed, ForwardBoomerangResults<TransitionFunction> results);

    void extractedBoomerangResults(
            ExtractParameterQuery query, BackwardBoomerangResults<Weight.NoWeight> results);

    void collectedValues(
            IAnalysisSeed seed, Collection<CallSiteWithExtractedValue> collectedValues);

    void checkedConstraints(
            IAnalysisSeed seed,
            Collection<ISLConstraint> constraints,
            Collection<AbstractError> errors);

    void generatedPredicate(
            IAnalysisSeed fromSeed,
            EnsuredCrySLPredicate predicate,
            IAnalysisSeed toSeed,
            Statement statement);

    void ensuredPredicates(
            IAnalysisSeed seed,
            Multimap<Statement, Map.Entry<EnsuredCrySLPredicate, Integer>> predicates);
}
