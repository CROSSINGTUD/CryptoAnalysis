package crypto.listener;

import boomerang.results.BackwardBoomerangResults;
import boomerang.results.ForwardBoomerangResults;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import crypto.analysis.EnsuredCrySLPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractParameterQuery;
import crypto.rules.ISLConstraint;
import typestate.TransitionFunction;
import wpds.impl.Weight;

import java.util.Collection;
import java.util.Set;

public interface IResultsListener {

    void typestateAnalysisResults(IAnalysisSeed seed, ForwardBoomerangResults<TransitionFunction> results);

    void extractedBoomerangResults(ExtractParameterQuery query, BackwardBoomerangResults<Weight.NoWeight> results);

    void collectedValues(IAnalysisSeed seed, Collection<CallSiteWithExtractedValue> collectedValues);

    void checkedConstraints(IAnalysisSeed seed, Collection<ISLConstraint> constraints, Collection<AbstractError> errors);

    void ensuredPredicates(Table<Statement, Val, Set<EnsuredCrySLPredicate>> existingPredicates);
}
