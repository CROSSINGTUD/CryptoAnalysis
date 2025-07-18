/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package de.fraunhofer.iem.scanner;

import boomerang.scope.DataFlowScope;
import crypto.analysis.CryptoAnalysisDataFlowScope;
import crypto.analysis.CryptoScanner;
import crypto.exceptions.CryptoAnalysisException;
import crypto.exceptions.CryptoAnalysisParserException;
import crypto.reporting.Reporter;
import crypto.reporting.ReporterFactory;
import crypto.visualization.Visualizer;
import crysl.rule.CrySLRule;
import de.fraunhofer.iem.cryptoanalysis.scope.CryptoAnalysisScope;
import de.fraunhofer.iem.framework.FrameworkSetup;
import de.fraunhofer.iem.framework.OpalSetup;
import de.fraunhofer.iem.framework.SootSetup;
import de.fraunhofer.iem.framework.SootUpSetup;
import de.fraunhofer.iem.scanner.ScannerSettings.CallGraphAlgorithm;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sparse.SparsificationStrategy;

public class HeadlessJavaScanner extends CryptoScanner {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeadlessJavaScanner.class);

    private final ScannerSettings settings;

    public HeadlessJavaScanner(String applicationPath, String rulesetDirectory) {
        settings = new ScannerSettings();

        settings.setApplicationPath(applicationPath);
        settings.setRulesetPath(rulesetDirectory);
        settings.setReportFormats(new HashSet<>());
    }

    private HeadlessJavaScanner(ScannerSettings settings) {
        this.settings = settings;
    }

    public static HeadlessJavaScanner createFromCLISettings(String[] args)
            throws CryptoAnalysisParserException {
        ScannerSettings scannerSettings = new ScannerSettings();
        scannerSettings.parseSettingsFromCLI(args);

        return new HeadlessJavaScanner(scannerSettings);
    }

    @Override
    public SparsificationStrategy<?, ?> getSparsificationStrategy() {
        return switch (settings.getSparseStrategy()) {
            case NONE -> SparsificationStrategy.NONE;
            // case TYPE_BASED -> SparsificationStrategy.TYPE_BASED;
            // case ALIAS_AWARE -> SparsificationStrategy.ALIAS_AWARE;
            // TODO If fixed in Boomerang, enable the options again
            default ->
                    throw new UnsupportedOperationException(
                            "Sparsification Strategy not supported");
        };
    }

    @Override
    public int getTimeout() {
        return settings.getTimeout();
    }

    public void scan() {
        // Read rules
        LOGGER.info("Reading rules from {}", settings.getRulesetPath());
        Collection<CrySLRule> rules =
                readRules(settings.getRulesetPath(), settings.getAddClassPath());
        LOGGER.info("Found {} rules in {}", rules.size(), settings.getRulesetPath());

        // Initialize the reporters before the analysis to catch errors early
        Collection<Reporter> reporters =
                ReporterFactory.createReporters(
                        settings.getReportFormats(), settings.getReportDirectory(), rules);
        if (settings.isVisualization() && settings.getReportDirectory() == null) {
            throw new IllegalArgumentException(
                    "Cannot create visualization without existing report directory");
        }

        // Set up the framework
        DataFlowScope dataFlowScope =
                new CryptoAnalysisDataFlowScope(rules, settings.getIgnoredSections());
        CryptoAnalysisScope frameworkScope = initializeFrameworkScope(dataFlowScope);

        // Run the analysis
        super.scan(frameworkScope, rules, settings.getAddClassPath());

        // Report the errors
        for (Reporter reporter : reporters) {
            reporter.createAnalysisReport(
                    super.getDiscoveredSeeds(), super.getCollectedErrors(), super.getStatistics());
        }

        // Create visualization
        if (settings.isVisualization()) {
            try {
                Visualizer visualizer = new Visualizer(settings.getReportDirectory());
                visualizer.createVisualization(getDiscoveredSeeds());
            } catch (IOException e) {
                throw new CryptoAnalysisException(
                        "Couldn't create visualization: " + e.getMessage());
            }
        }
    }

    public CryptoAnalysisScope initializeFrameworkScope(DataFlowScope dataFlowScope) {
        FrameworkSetup frameworkSetup = setupFramework(dataFlowScope);
        frameworkSetup.initializeFramework();

        super.getAnalysisReporter().beforeCallGraphConstruction();
        CryptoAnalysisScope frameworkScope = frameworkSetup.createFrameworkScope();
        super.getAnalysisReporter()
                .afterCallGraphConstruction(frameworkScope.asFrameworkScope().getCallGraph());

        return frameworkScope;
    }

    private FrameworkSetup setupFramework(DataFlowScope dataFlowScope) {
        return switch (settings.getFramework()) {
            case SOOT ->
                    new SootSetup(
                            settings.getApplicationPath(),
                            settings.getCallGraph(),
                            settings.getAddClassPath(),
                            dataFlowScope);
            case SOOT_UP ->
                    new SootUpSetup(
                            settings.getApplicationPath(), settings.getCallGraph(), dataFlowScope);
            case OPAL ->
                    new OpalSetup(
                            settings.getApplicationPath(), settings.getCallGraph(), dataFlowScope);
        };
    }

    public String getApplicationPath() {
        return settings.getApplicationPath();
    }

    public String getRulesetPath() {
        return settings.getRulesetPath();
    }

    public ScannerSettings.Framework getFramework() {
        return settings.getFramework();
    }

    public void setFramework(ScannerSettings.Framework framework) {
        settings.setFramework(framework);
    }

    public CallGraphAlgorithm getCallGraphAlgorithm() {
        return settings.getCallGraph();
    }

    public void setCallGraphAlgorithm(CallGraphAlgorithm callGraphAlgorithm) {
        settings.setCallGraph(callGraphAlgorithm);
    }

    public String getAddClassPath() {
        return settings.getAddClassPath();
    }

    public void setAddClassPath(String sootClassPath) {
        settings.setAddClassPath(sootClassPath);
    }

    public String getReportDirectory() {
        return settings.getReportDirectory();
    }

    public void setReportDirectory(String reportDirectory) {
        settings.setReportDirectory(reportDirectory);
    }

    public Collection<Reporter.ReportFormat> getReportFormats() {
        return settings.getReportFormats();
    }

    public void setReportFormats(Reporter.ReportFormat... formats) {
        setReportFormats(Arrays.asList(formats));
    }

    public void setReportFormats(Collection<Reporter.ReportFormat> reportFormats) {
        settings.setReportFormats(reportFormats);
    }

    public boolean isVisualization() {
        return settings.isVisualization();
    }

    public void setVisualization(boolean visualization) {
        settings.setVisualization(visualization);
    }

    public Collection<String> getIgnoredSections() {
        return settings.getIgnoredSections();
    }

    public void setIgnoredSections(Collection<String> ignoredSections) {
        settings.setIgnoredSections(ignoredSections);
    }

    public ScannerSettings.SparseStrategy getSparseStrategy() {
        return settings.getSparseStrategy();
    }

    public void setSparseStrategy(ScannerSettings.SparseStrategy strategy) {
        settings.setSparseStrategy(strategy);
    }

    public void setTimeout(int timeout) {
        settings.setTimeout(timeout);
    }
}
