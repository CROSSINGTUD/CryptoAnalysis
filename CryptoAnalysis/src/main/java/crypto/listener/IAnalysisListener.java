package crypto.listener;

import boomerang.scope.CallGraph;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.extractparameter.ExtractParameterQuery;
import java.util.Collection;

public interface IAnalysisListener {

    void beforeAnalysis();

    void afterAnalysis();

    void beforeCallGraphConstruction();

    void afterCallGraphConstruction(CallGraph callGraph);

    void beforeTypestateAnalysis();

    void afterTypestateAnalysis();

    void beforeTriggeringBoomerangQuery(ExtractParameterQuery query);

    void afterTriggeringBoomerangQuery(ExtractParameterQuery query);

    void onDiscoveredSeeds(Collection<IAnalysisSeed> discoveredSeeds);

    void onSeedStarted(IAnalysisSeed analysisSeed);

    void onSeedFinished(IAnalysisSeed analysisSeed);

    void onTypestateAnalysisTimeout(IAnalysisSeed analysisSeed);

    void onExtractParameterAnalysisTimeout(Val parameter, Statement statement);

    void beforeConstraintsCheck(IAnalysisSeed analysisSeed);

    void afterConstraintsCheck(IAnalysisSeed analysisSeed, int violatedConstraints);

    void beforePredicateCheck();

    void afterPredicateCheck();

    void onReportedError(IAnalysisSeed analysisSeed, AbstractError error);

    void addProgress(int current, int total);
}
