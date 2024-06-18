package crypto.analysis;

import crypto.analysis.errors.AbstractError;
import crypto.listener.IAnalysisListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class AnalysisPrinter implements IAnalysisListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisPrinter.class);

    @Override
    public void beforeAnalysis() {
        LOGGER.debug("Starting Scan...");
    }

    @Override
    public void afterAnalysis() {
        LOGGER.debug("Finished Scan");
    }

    @Override
    public void beforeTypestateAnalysis() {
        LOGGER.debug("Starting Typestate Analysis");
    }

    @Override
    public void afterTypestateAnalysis() {
        LOGGER.debug("Typestate Analysis finished");
    }

    @Override
    public void onDiscoveredSeeds(Collection<IAnalysisSeed> discoveredSeeds) {
        LOGGER.info("Discovered {} seeds", discoveredSeeds.size());
    }

    @Override
    public void onSeedStarted(IAnalysisSeed analysisSeed) {
        LOGGER.debug("Starting to analyze {}", analysisSeed);
    }

    @Override
    public void onSeedFinished(IAnalysisSeed analysisSeed) {
        LOGGER.debug("Finished analyzing {}", analysisSeed);
    }

    @Override
    public void beforeConstraintsCheck(IAnalysisSeed analysisSeed) {
        LOGGER.debug("Starting constraints check for {}", analysisSeed);
    }

    @Override
    public void afterConstraintsCheck(IAnalysisSeed analysisSeed, int violatedConstraints) {
        LOGGER.debug("Violated constraints for {}: {}", analysisSeed, violatedConstraints);
    }

    @Override
    public void beforePredicateCheck() {
        LOGGER.debug("Start predicate checks");
    }

    @Override
    public void afterPredicateCheck() {
        LOGGER.debug("Finished predicate checks");
    }

    @Override
    public void onReportedError(IAnalysisSeed analysisSeed, AbstractError error) {
        LOGGER.debug("Found error {} on {}",error, analysisSeed);
    }

    @Override
    public void addProgress(int current, int total) {
        LOGGER.info("Analyzed seeds: {} of {}", current, total);
    }
}
