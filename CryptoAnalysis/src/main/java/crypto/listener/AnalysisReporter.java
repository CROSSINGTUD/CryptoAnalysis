package crypto.listener;

import boomerang.results.BackwardBoomerangResults;
import boomerang.results.ForwardBoomerangResults;
import boomerang.scene.CallGraph;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import com.google.common.collect.Multimap;
import crypto.analysis.EnsuredCrySLPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.CallToError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.HardCodedError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.InstanceOfError;
import crypto.analysis.errors.NeverTypeOfError;
import crypto.analysis.errors.NoCallToError;
import crypto.analysis.errors.PredicateContradictionError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import crypto.analysis.errors.UncaughtExceptionError;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.extractparameter.ExtractParameterQuery;
import crysl.rule.CrySLRule;
import crysl.rule.ISLConstraint;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import typestate.TransitionFunction;
import wpds.impl.Weight;

public class AnalysisReporter {

    private final Collection<IAnalysisListener> analysisListeners;
    private final Collection<IErrorListener> errorListeners;
    private final Collection<IResultsListener> resultsListeners;

    public AnalysisReporter() {
        analysisListeners = new HashSet<>();
        errorListeners = new HashSet<>();
        resultsListeners = new HashSet<>();
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

    public void beforeReadingRuleset(String rulesetPath) {
        for (IAnalysisListener listener : analysisListeners) {
            listener.beforeReadingRuleset(rulesetPath);
        }
    }

    public void afterReadingRuleset(String rulesetPath, Collection<CrySLRule> rules) {
        for (IAnalysisListener listener : analysisListeners) {
            listener.afterReadingRuleset(rulesetPath, rules);
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

    public void beforeTriggeringBoomerangQuery(ExtractParameterQuery query) {
        for (IAnalysisListener listener : analysisListeners) {
            listener.beforeTriggeringBoomerangQuery(query);
        }
    }

    public void afterTriggeringBoomerangQuery(ExtractParameterQuery query) {
        for (IAnalysisListener listener : analysisListeners) {
            listener.afterTriggeringBoomerangQuery(query);
        }
    }

    public void extractedBoomerangResults(
            ExtractParameterQuery query, BackwardBoomerangResults<Weight.NoWeight> results) {
        for (IResultsListener listener : resultsListeners) {
            listener.extractedBoomerangResults(query, results);
        }
    }

    public void onDiscoveredSeeds(Collection<IAnalysisSeed> discoveredSeeds) {
        for (IAnalysisListener analysisListener : analysisListeners) {
            analysisListener.onDiscoveredSeeds(discoveredSeeds);
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

    public void onGeneratedPredicate(
            IAnalysisSeed fromSeed,
            EnsuredCrySLPredicate predicate,
            IAnalysisSeed toPred,
            Statement statement) {
        for (IResultsListener listener : resultsListeners) {
            listener.generatedPredicate(fromSeed, predicate, toPred, statement);
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

    public void collectedValues(
            IAnalysisSeed seed, Collection<CallSiteWithExtractedValue> collectedValues) {
        for (IResultsListener resultsListener : resultsListeners) {
            resultsListener.collectedValues(seed, collectedValues);
        }
    }

    public void checkedConstraints(
            IAnalysisSeed seed,
            Collection<ISLConstraint> constraints,
            Collection<AbstractError> errors) {
        for (IResultsListener resultsListener : resultsListeners) {
            resultsListener.checkedConstraints(seed, constraints, errors);
        }
    }

    public void ensuredPredicates(
            IAnalysisSeed seed,
            Multimap<Statement, Map.Entry<EnsuredCrySLPredicate, Integer>> predicates) {
        for (IResultsListener listener : resultsListeners) {
            listener.ensuredPredicates(seed, predicates);
        }
    }

    public void reportError(IAnalysisSeed seed, AbstractError error) {
        seed.setSecure(false);

        for (IAnalysisListener analysisListener : analysisListeners) {
            analysisListener.onReportedError(seed, error);
        }

        for (IErrorListener errorListener : errorListeners) {
            if (error instanceof CallToError) {
                CallToError callToError = (CallToError) error;
                errorListener.reportError(callToError);
            } else if (error instanceof ConstraintError) {
                ConstraintError constraintError = (ConstraintError) error;
                errorListener.reportError(constraintError);
            } else if (error instanceof ForbiddenMethodError) {
                ForbiddenMethodError forbiddenMethodError = (ForbiddenMethodError) error;
                errorListener.reportError(forbiddenMethodError);
            } else if (error instanceof HardCodedError) {
                HardCodedError hardCodedError = (HardCodedError) error;
                errorListener.reportError(hardCodedError);
            } else if (error instanceof ImpreciseValueExtractionError) {
                ImpreciseValueExtractionError impreciseError =
                        (ImpreciseValueExtractionError) error;
                errorListener.reportError(impreciseError);
            } else if (error instanceof IncompleteOperationError) {
                IncompleteOperationError incompleteError = (IncompleteOperationError) error;
                errorListener.reportError(incompleteError);
            } else if (error instanceof InstanceOfError) {
                InstanceOfError instanceOfError = (InstanceOfError) error;
                errorListener.reportError(instanceOfError);
            } else if (error instanceof NeverTypeOfError) {
                NeverTypeOfError neverTypeOfError = (NeverTypeOfError) error;
                errorListener.reportError(neverTypeOfError);
            } else if (error instanceof NoCallToError) {
                NoCallToError noCallToError = (NoCallToError) error;
                errorListener.reportError(noCallToError);
            } else if (error instanceof PredicateContradictionError) {
                PredicateContradictionError contradictionError =
                        (PredicateContradictionError) error;
                errorListener.reportError(contradictionError);
            } else if (error instanceof RequiredPredicateError) {
                RequiredPredicateError predicateError = (RequiredPredicateError) error;
                errorListener.reportError(predicateError);
            } else if (error instanceof TypestateError) {
                TypestateError typestateError = (TypestateError) error;
                errorListener.reportError(typestateError);
            } else if (error instanceof UncaughtExceptionError) {
                UncaughtExceptionError exceptionError = (UncaughtExceptionError) error;
                errorListener.reportError(exceptionError);
            } else {
                errorListener.reportError(error);
            }
        }
    }
}
