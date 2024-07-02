package crypto.listener;

import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;

import java.util.Collection;

public interface IAnalysisListener {

    void beforeAnalysis();

    void afterAnalysis();

    void beforeTypestateAnalysis();

    void afterTypestateAnalysis();

    void onDiscoveredSeeds(Collection<IAnalysisSeed> discoveredSeeds);

    void onSeedStarted(IAnalysisSeed analysisSeed);

    void onSeedFinished(IAnalysisSeed analysisSeed);

    void beforeConstraintsCheck(IAnalysisSeed analysisSeed);

    void afterConstraintsCheck(IAnalysisSeed analysisSeed, int violatedConstraints);

    void beforePredicateCheck();

    void afterPredicateCheck();

    void onReportedError(IAnalysisSeed analysisSeed, AbstractError error);

    void addProgress(int current, int total);
}
