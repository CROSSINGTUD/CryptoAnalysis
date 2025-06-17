/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.listener;

import boomerang.results.BackwardBoomerangResults;
import boomerang.results.ForwardBoomerangResults;
import boomerang.scope.CallGraph;
import boomerang.scope.Statement;
import com.google.common.collect.Multimap;
import crypto.analysis.IAnalysisSeed;
import crypto.constraints.EvaluableConstraint;
import crypto.extractparameter.ExtractParameterQuery;
import crypto.extractparameter.ParameterWithExtractedValues;
import crypto.predicates.EnsuredPredicate;
import crypto.predicates.UnEnsuredPredicate;
import java.util.Collection;
import typestate.TransitionFunction;
import wpds.impl.NoWeight;

public interface IResultsListener {

    void constructedCallGraph(CallGraph callGraph);

    void typestateAnalysisResults(
            IAnalysisSeed seed, ForwardBoomerangResults<TransitionFunction> results);

    void discoveredSeeds(Collection<IAnalysisSeed> seeds);

    void extractedBoomerangResults(
            ExtractParameterQuery query, BackwardBoomerangResults<NoWeight> results);

    void extractedParameterValues(
            IAnalysisSeed seed, Multimap<Statement, ParameterWithExtractedValues> extractedValues);

    void evaluatedConstraint(
            IAnalysisSeed seed,
            EvaluableConstraint constraint,
            EvaluableConstraint.EvaluationResult result);

    void ensuredPredicates(IAnalysisSeed seed, Multimap<Statement, EnsuredPredicate> predicates);

    void unEnsuredPredicates(
            IAnalysisSeed seed, Multimap<Statement, UnEnsuredPredicate> predicates);
}
