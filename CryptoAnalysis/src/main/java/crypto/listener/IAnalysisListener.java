package crypto.listener;

import boomerang.scene.CallGraph;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.extractparameter.ExtractParameterQuery;

import java.util.Collection;

public interface IAnalysisListener {

    void beforeAnalysis();

    void afterAnalysis();

    void beforeTypestateAnalysis();

    void afterTypestateAnalysis();

    void beforeCallGraphConstruction();

    void afterCallGraphConstruction(CallGraph callGraph);

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
