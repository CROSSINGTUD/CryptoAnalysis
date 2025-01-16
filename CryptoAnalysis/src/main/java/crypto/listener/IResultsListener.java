package crypto.listener;

import boomerang.results.BackwardBoomerangResults;
import boomerang.results.ForwardBoomerangResults;
import boomerang.scene.CallGraph;
import boomerang.scene.Statement;
import com.google.common.collect.Multimap;
import crypto.analysis.IAnalysisSeed;
import crypto.constraints.EvaluableConstraint;
import crypto.extractparameter.ExtractParameterQuery;
import crypto.extractparameter.ParameterWithExtractedValues;
import crypto.predicates.EnsuredPredicate;
import crypto.predicates.UnEnsuredPredicate;
import java.util.Collection;
import typestate.TransitionFunction;
import wpds.impl.Weight;

public interface IResultsListener {

    void constructedCallGraph(CallGraph callGraph);

    void typestateAnalysisResults(
            IAnalysisSeed seed, ForwardBoomerangResults<TransitionFunction> results);

    void extractedBoomerangResults(
            ExtractParameterQuery query, BackwardBoomerangResults<Weight.NoWeight> results);

    void extractedParameterValues(
            IAnalysisSeed seed, Collection<ParameterWithExtractedValues> extractedValues);

    void evaluatedConstraint(
            IAnalysisSeed seed,
            EvaluableConstraint constraint,
            EvaluableConstraint.EvaluationResult result);

    void ensuredPredicates(IAnalysisSeed seed, Multimap<Statement, EnsuredPredicate> predicates);

    void unEnsuredPredicates(
            IAnalysisSeed seed, Multimap<Statement, UnEnsuredPredicate> predicates);
}
