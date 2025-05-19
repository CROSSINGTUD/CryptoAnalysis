/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.analysis;

import boomerang.scope.FrameworkScope;
import boomerang.scope.Method;
import boomerang.scope.WrappedClass;
import com.google.common.collect.Table;
import crypto.analysis.errors.AbstractError;
import crypto.exceptions.CryptoAnalysisException;
import crypto.listener.AnalysisPrinter;
import crypto.listener.AnalysisReporter;
import crypto.listener.AnalysisStatistics;
import crypto.listener.ErrorCollector;
import crypto.listener.IAnalysisListener;
import crypto.listener.IErrorListener;
import crypto.listener.IResultsListener;
import crypto.predicates.PredicateAnalysis;
import crysl.CrySLParser;
import crysl.rule.CrySLRule;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import sparse.SparsificationStrategy;

public class CryptoScanner {

    private final AnalysisReporter analysisReporter;
    private final Map<IAnalysisSeed, IAnalysisSeed> discoveredSeeds;

    private AnalysisPrinter analysisPrinter;
    private ErrorCollector errorCollector;

    public CryptoScanner() {
        this.analysisReporter = new AnalysisReporter();
        this.discoveredSeeds = new HashMap<>();

        analysisPrinter = new AnalysisPrinter();
        addAnalysisListener(analysisPrinter);

        errorCollector = new ErrorCollector();
        addErrorListener(errorCollector);
    }

    public final Collection<CrySLRule> readRules(String rulesetPath) {
        return readRules(rulesetPath, "");
    }

    public final Collection<CrySLRule> readRules(String rulesetPath, String classPath) {
        try {
            if (classPath.isEmpty()) {
                CrySLParser parser = new CrySLParser();
                return parser.parseRulesFromPath(rulesetPath);
            } else {
                Collection<String> pathSplits = Set.of(classPath.split(File.pathSeparator));
                Collection<Path> paths = pathSplits.stream().map(Path::of).toList();

                CrySLParser parser = new CrySLParser(paths);
                return parser.parseRulesFromPath(rulesetPath);
            }
        } catch (IOException e) {
            throw new CryptoAnalysisException("Could not read rules: " + e.getMessage());
        }
    }

    public final void scan(FrameworkScope frameworkScope, Collection<CrySLRule> ruleset) {
        // Start analysis
        analysisReporter.beforeAnalysis();

        SeedGenerator generator = new SeedGenerator(this, frameworkScope, ruleset);
        List<IAnalysisSeed> seeds = new ArrayList<>(generator.computeSeeds());
        analysisReporter.onDiscoveredSeeds(seeds);

        for (IAnalysisSeed seed : seeds) {
            discoveredSeeds.put(seed, seed);
        }

        for (int i = 0; i < seeds.size(); i++) {
            seeds.get(i).execute();
            analysisReporter.addProgress(i + 1, seeds.size());
        }

        analysisReporter.beforePredicateCheck();
        PredicateAnalysis analysis = new PredicateAnalysis();
        analysis.checkPredicates(seeds);
        analysisReporter.afterPredicateCheck();

        analysisReporter.afterAnalysis();
    }

    public final void reset() {
        analysisReporter.clear();

        analysisPrinter = new AnalysisPrinter();
        addAnalysisListener(analysisPrinter);

        errorCollector = new ErrorCollector();
        addErrorListener(errorCollector);
    }

    public final void addAnalysisListener(IAnalysisListener analysisListener) {
        analysisReporter.addAnalysisListener(analysisListener);
    }

    public final void addErrorListener(IErrorListener errorListener) {
        analysisReporter.addErrorListener(errorListener);
    }

    public final void addResultsListener(IResultsListener resultsListener) {
        analysisReporter.addResultsListener(resultsListener);
    }

    public final AnalysisReporter getAnalysisReporter() {
        return analysisReporter;
    }

    public final Table<WrappedClass, Method, Set<AbstractError>> getCollectedErrors() {
        return errorCollector.getErrorCollection();
    }

    public final Collection<IAnalysisSeed> getDiscoveredSeeds() {
        return discoveredSeeds.keySet();
    }

    public final Collection<AnalysisSeedWithSpecification> getAnalysisSeedsWithSpec() {
        Collection<AnalysisSeedWithSpecification> seeds = new HashSet<>();

        for (IAnalysisSeed seed : discoveredSeeds.keySet()) {
            if (seed instanceof AnalysisSeedWithSpecification) {
                seeds.add((AnalysisSeedWithSpecification) seed);
            }
        }
        return seeds;
    }

    public final AnalysisStatistics getStatistics() {
        return analysisPrinter.getStatistics();
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *               Methods that may or must be overridden by subclasses                *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public SparsificationStrategy<?, ?> getSparsificationStrategy() {
        return SparsificationStrategy.NONE;
    }

    public int getTimeout() {
        return -1;
    }
}
