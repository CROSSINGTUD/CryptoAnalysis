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

import boomerang.scope.CallGraph;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import com.google.common.base.Stopwatch;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.extractparameter.ExtractParameterQuery;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnalysisPrinter implements IAnalysisListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisPrinter.class);

    private final AnalysisStatistics statistics = new AnalysisStatistics();

    private final Stopwatch analysisWatch = Stopwatch.createUnstarted();
    private final Stopwatch callGraphWatch = Stopwatch.createUnstarted();
    private final Stopwatch typestateWatch = Stopwatch.createUnstarted();

    public AnalysisStatistics getStatistics() {
        return statistics;
    }

    @Override
    public void beforeAnalysis() {
        LOGGER.info("Starting analysis...");

        if (!analysisWatch.isRunning()) {
            analysisWatch.start();
        }
    }

    @Override
    public void afterAnalysis() {
        if (analysisWatch.isRunning()) {
            analysisWatch.stop();
        }

        statistics.setAnalysisTime(analysisWatch.toString());

        LOGGER.info("Finished Analysis in {}", analysisWatch);
    }

    @Override
    public void beforeCallGraphConstruction() {
        LOGGER.info("Constructing Call Graph...");

        if (!callGraphWatch.isRunning()) {
            callGraphWatch.start();
        }
    }

    @Override
    public void afterCallGraphConstruction(CallGraph callGraph) {
        if (callGraphWatch.isRunning()) {
            callGraphWatch.stop();
        }

        statistics.setCallGraphTime(callGraphWatch.toString());
        statistics.setCallGraph(callGraph);

        LOGGER.info("Constructed Call Graph in {}", callGraphWatch);
    }

    @Override
    public void beforeTypestateAnalysis() {
        LOGGER.info("Starting Typestate Analysis...");

        if (!typestateWatch.isRunning()) {
            typestateWatch.start();
        }
    }

    @Override
    public void afterTypestateAnalysis() {
        if (typestateWatch.isRunning()) {
            typestateWatch.stop();
        }

        statistics.setTypestateTime(typestateWatch.toString());

        LOGGER.info("Finished Typestate Analysis in {}", typestateWatch);
    }

    @Override
    public void onDiscoveredSeeds(Collection<IAnalysisSeed> discoveredSeeds) {
        LOGGER.info("Analyzing {} seeds", discoveredSeeds.size());
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
    public void onTypestateAnalysisTimeout(IAnalysisSeed analysisSeed) {
        LOGGER.warn(
                "Seed {} timed out while typestate analysis. Consider increasing the timeout with '--timeout' or 'setTimeout'",
                analysisSeed);
    }

    @Override
    public void beforeTriggeringBoomerangQuery(ExtractParameterQuery query) {
        LOGGER.debug(
                "Triggering Boomerang query for value {} @ {}",
                query.var(),
                query.cfgEdge().getTarget());
    }

    @Override
    public void afterTriggeringBoomerangQuery(ExtractParameterQuery query) {
        LOGGER.debug(
                "Finished Boomerang query for value {} @ {}",
                query.var(),
                query.cfgEdge().getTarget());
    }

    @Override
    public void onExtractParameterAnalysisTimeout(Val param, Statement statement) {
        LOGGER.warn(
                "Seed timed out while extracting parameter {} @ {}. Consider increasing the timeout with '--timeout' or 'setTimeout'",
                param,
                statement);
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
        LOGGER.debug("Found {} on {}", error.getClass().getSimpleName(), analysisSeed);
    }

    @Override
    public void addProgress(int current, int total) {
        LOGGER.info("Analyzed seeds: {} of {}", current, total);
    }
}
