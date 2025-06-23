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

import boomerang.BackwardQuery;
import boomerang.results.BackwardBoomerangResults;
import boomerang.results.ForwardBoomerangResults;
import boomerang.scope.CallGraph;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import com.google.common.collect.Multimap;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.AlternativeReqPredicateError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.PredicateContradictionError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import crypto.analysis.errors.UncaughtExceptionError;
import crypto.constraints.EvaluableConstraint;
import crypto.extractparameter.ParameterWithExtractedValues;
import crypto.predicates.EnsuredPredicate;
import crypto.predicates.UnEnsuredPredicate;
import java.util.Collection;
import java.util.HashSet;
import typestate.TransitionFunction;
import wpds.impl.NoWeight;

public class AnalysisReporter {

    private final Collection<IAnalysisListener> analysisListeners;
    private final Collection<IErrorListener> errorListeners;
    private final Collection<IResultsListener> resultsListeners;

    public AnalysisReporter() {
        analysisListeners = new HashSet<>();
        errorListeners = new HashSet<>();
        resultsListeners = new HashSet<>();
    }

    public void clear() {
        analysisListeners.clear();
        errorListeners.clear();
        resultsListeners.clear();
    }

    public void addAnalysisListener(IAnalysisListener analysisListener) {
        analysisListeners.add(analysisListener);
    }

    public void addErrorListener(IErrorListener errorListener) {
        errorListeners.add(errorListener);
    }

    public void addResultsListener(IResultsListener resultsListener) {
        resultsListeners.add(resultsListener);
    }

    public void beforeAnalysis() {
        for (IAnalysisListener analysisListener : analysisListeners) {
            analysisListener.beforeAnalysis();
        }
    }

    public void afterAnalysis() {
        for (IAnalysisListener analysisListener : analysisListeners) {
            analysisListener.afterAnalysis();
        }
    }

    public void beforeCallGraphConstruction() {
        for (IAnalysisListener analysisListener : analysisListeners) {
            analysisListener.beforeCallGraphConstruction();
        }
    }

    public void afterCallGraphConstruction(CallGraph callGraph) {
        for (IAnalysisListener analysisListener : analysisListeners) {
            analysisListener.afterCallGraphConstruction(callGraph);
        }

        for (IResultsListener resultsListener : resultsListeners) {
            resultsListener.constructedCallGraph(callGraph);
        }
    }

    public void beforeTypestateAnalysis() {
        for (IAnalysisListener analysisListener : analysisListeners) {
            analysisListener.beforeTypestateAnalysis();
        }
    }

    public void afterTypestateAnalysis() {
        for (IAnalysisListener analysisListener : analysisListeners) {
            analysisListener.afterTypestateAnalysis();
        }
    }

    public void beforeTriggeringBoomerangQuery(BackwardQuery query) {
        for (IAnalysisListener listener : analysisListeners) {
            listener.beforeTriggeringBoomerangQuery(query);
        }
    }

    public void afterTriggeringBoomerangQuery(BackwardQuery query) {
        for (IAnalysisListener listener : analysisListeners) {
            listener.afterTriggeringBoomerangQuery(query);
        }
    }

    public void extractedBoomerangResults(
            BackwardQuery query, BackwardBoomerangResults<NoWeight> results) {
        for (IResultsListener listener : resultsListeners) {
            listener.extractedBoomerangResults(query, results);
        }
    }

    public void onDiscoveredSeeds(Collection<IAnalysisSeed> discoveredSeeds) {
        for (IAnalysisListener analysisListener : analysisListeners) {
            analysisListener.onDiscoveredSeeds(discoveredSeeds);
        }

        for (IResultsListener resultsListener : resultsListeners) {
            resultsListener.discoveredSeeds(discoveredSeeds);
        }
    }

    public void onSeedStarted(IAnalysisSeed analysisSeed) {
        for (IAnalysisListener analysisListener : analysisListeners) {
            analysisListener.onSeedStarted(analysisSeed);
        }
    }

    public void onSeedFinished(IAnalysisSeed analysisSeed) {
        for (IAnalysisListener analysisListener : analysisListeners) {
            analysisListener.onSeedFinished(analysisSeed);
        }
    }

    public void onTypestateAnalysisTimeout(IAnalysisSeed analysisSeed) {
        for (IAnalysisListener analysisListener : analysisListeners) {
            analysisListener.onTypestateAnalysisTimeout(analysisSeed);
        }
    }

    public void onExtractParameterAnalysisTimeout(Val parameter, Statement statement) {
        for (IAnalysisListener analysisListener : analysisListeners) {
            analysisListener.onExtractParameterAnalysisTimeout(parameter, statement);
        }
    }

    public void beforeConstraintsCheck(IAnalysisSeed analysisSeed) {
        for (IAnalysisListener analysisListener : analysisListeners) {
            analysisListener.beforeConstraintsCheck(analysisSeed);
        }
    }

    public void afterConstraintsCheck(IAnalysisSeed analysisSeed, int violatedConstraints) {
        for (IAnalysisListener analysisListener : analysisListeners) {
            analysisListener.afterConstraintsCheck(analysisSeed, violatedConstraints);
        }
    }

    public void onEvaluatedConstraint(
            IAnalysisSeed seed,
            EvaluableConstraint constraint,
            EvaluableConstraint.EvaluationResult result) {
        for (IResultsListener listener : resultsListeners) {
            listener.evaluatedConstraint(seed, constraint, result);
        }
    }

    public void onEvaluatedPredicate(
            IAnalysisSeed seed,
            EvaluableConstraint constraint,
            EvaluableConstraint.EvaluationResult result) {}

    public void beforePredicateCheck() {
        for (IAnalysisListener analysisListener : analysisListeners) {
            analysisListener.beforePredicateCheck();
        }
    }

    public void afterPredicateCheck() {
        for (IAnalysisListener analysisListener : analysisListeners) {
            analysisListener.afterPredicateCheck();
        }
    }

    public void addProgress(int current, int total) {
        for (IAnalysisListener analysisListener : analysisListeners) {
            analysisListener.addProgress(current, total);
        }
    }

    public void typestateAnalysisResults(
            IAnalysisSeed seed, ForwardBoomerangResults<TransitionFunction> results) {
        for (IResultsListener resultsListener : resultsListeners) {
            resultsListener.typestateAnalysisResults(seed, results);
        }
    }

    public void extractedParameterValues(
            IAnalysisSeed seed, Multimap<Statement, ParameterWithExtractedValues> extractedValues) {
        for (IResultsListener resultsListener : resultsListeners) {
            resultsListener.extractedParameterValues(seed, extractedValues);
        }
    }

    public void ensuredPredicates(
            IAnalysisSeed seed, Multimap<Statement, EnsuredPredicate> predicates) {
        for (IResultsListener listener : resultsListeners) {
            listener.ensuredPredicates(seed, predicates);
        }
    }

    public void unEnsuredPredicates(
            IAnalysisSeed seed, Multimap<Statement, UnEnsuredPredicate> predicates) {
        for (IResultsListener listener : resultsListeners) {
            listener.unEnsuredPredicates(seed, predicates);
        }
    }

    public void reportError(IAnalysisSeed seed, AbstractError error) {
        for (IAnalysisListener analysisListener : analysisListeners) {
            analysisListener.onReportedError(seed, error);
        }

        for (IErrorListener errorListener : errorListeners) {
            if (error instanceof ConstraintError constraintError) {
                errorListener.reportError(constraintError);
            } else if (error instanceof ForbiddenMethodError forbiddenMethodError) {
                errorListener.reportError(forbiddenMethodError);
            } else if (error instanceof ImpreciseValueExtractionError impreciseError) {
                errorListener.reportError(impreciseError);
            } else if (error instanceof IncompleteOperationError incompleteError) {
                errorListener.reportError(incompleteError);
            } else if (error instanceof PredicateContradictionError contradictionError) {
                errorListener.reportError(contradictionError);
            } else if (error instanceof RequiredPredicateError predicateError) {
                errorListener.reportError(predicateError);
            } else if (error instanceof AlternativeReqPredicateError predicateError) {
                errorListener.reportError(predicateError);
            } else if (error instanceof TypestateError typestateError) {
                errorListener.reportError(typestateError);
            } else if (error instanceof UncaughtExceptionError exceptionError) {
                errorListener.reportError(exceptionError);
            } else {
                errorListener.reportError(error);
            }
        }
    }
}
