/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package de.fraunhofer.iem.android;

import boomerang.scope.DataFlowScope;
import crypto.analysis.CryptoScanner;
import crypto.exceptions.CryptoAnalysisParserException;
import crypto.reporting.Reporter;
import crypto.reporting.ReporterFactory;
import crysl.rule.CrySLRule;
import de.fraunhofer.iem.cryptoanalysis.scope.CryptoAnalysisScope;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeadlessAndroidScanner extends CryptoScanner {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeadlessAndroidScanner.class);

    private final AndroidSettings settings;

    public HeadlessAndroidScanner(
            String apkFile, String platformsDirectory, String rulesetDirectory) {
        settings = new AndroidSettings();

        settings.setApkFile(apkFile);
        settings.setPlatformDirectory(platformsDirectory);
        settings.setRulesetDirectory(rulesetDirectory);
        settings.setReportFormats(new HashSet<>());
    }

    private HeadlessAndroidScanner(AndroidSettings settings) {
        this.settings = settings;
    }

    public static HeadlessAndroidScanner createFromCLISettings(String[] args)
            throws CryptoAnalysisParserException {
        AndroidSettings androidSettings = new AndroidSettings();
        androidSettings.parseSettingsFromCLI(args);

        return new HeadlessAndroidScanner(androidSettings);
    }

    public void scan() {
        LOGGER.info("Reading rules from {}", settings.getRulesetDirectory());
        Collection<CrySLRule> rules = super.readRules(settings.getRulesetDirectory());
        LOGGER.info("Found {} rules in {}", rules.size(), settings.getRulesetDirectory());

        // Initialize the reporters before the analysis to catch errors early
        Collection<Reporter> reporters =
                ReporterFactory.createReporters(
                        settings.getReportFormats(), settings.getReportPath(), rules);

        // Setup FlowDroid
        FlowDroidSetup flowDroidSetup =
                new FlowDroidSetup(
                        settings.getApkFile(),
                        settings.getPlatformDirectory(),
                        settings.getCallGraphAlgorithm());
        flowDroidSetup.setupFlowDroid();
        additionalFrameworkSetup();

        DataFlowScope dataFlowScope = new AndroidDataFlowScope(rules, Collections.emptySet());
        super.getAnalysisReporter().beforeCallGraphConstruction();
        CryptoAnalysisScope frameworkScope = flowDroidSetup.createFrameworkScope(dataFlowScope);
        super.getAnalysisReporter()
                .afterCallGraphConstruction(frameworkScope.asFrameworkScope().getCallGraph());

        // Run the analysis
        super.scan(frameworkScope, rules);

        // Report the errors
        for (Reporter reporter : reporters) {
            reporter.createAnalysisReport(
                    super.getDiscoveredSeeds(), super.getCollectedErrors(), super.getStatistics());
        }
    }

    public String getApkFile() {
        return settings.getApkFile();
    }

    public String getPlatformDirectory() {
        return settings.getPlatformDirectory();
    }

    public String getRulesetPath() {
        return settings.getRulesetDirectory();
    }

    public AndroidSettings.CallGraphAlgorithm getCallGraphAlgorithm() {
        return settings.getCallGraphAlgorithm();
    }

    public void setCallGraphAlgorithm(AndroidSettings.CallGraphAlgorithm algorithm) {
        settings.setCallGraphAlgorithm(algorithm);
    }

    public Collection<Reporter.ReportFormat> getReportFormats() {
        return settings.getReportFormats();
    }

    public void setReportFormats(Reporter.ReportFormat... reportFormats) {
        setReportFormats(Arrays.asList(reportFormats));
    }

    public void setReportFormats(Collection<Reporter.ReportFormat> reportFormats) {
        settings.setReportFormats(reportFormats);
    }

    public String getReportDirectory() {
        return settings.getReportPath();
    }

    public void setReportDirectory(String reportDirectory) {
        settings.setReportPath(reportDirectory);
    }

    public boolean isVisualization() {
        return settings.isVisualization();
    }

    public void setVisualization(boolean visualization) {
        settings.setVisualization(visualization);
    }

    public void additionalFrameworkSetup() {}
}
