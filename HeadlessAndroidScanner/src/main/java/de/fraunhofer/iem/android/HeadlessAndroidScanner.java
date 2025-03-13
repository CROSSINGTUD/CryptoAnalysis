package de.fraunhofer.iem.android;

import boomerang.scope.DataFlowScope;
import boomerang.scope.FrameworkScope;
import crypto.analysis.CryptoAnalysisDataFlowScope;
import crypto.analysis.CryptoScanner;
import crypto.exceptions.CryptoAnalysisParserException;
import crypto.reporting.Reporter;
import crypto.reporting.ReporterFactory;
import crysl.rule.CrySLRule;
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
                new FlowDroidSetup(settings.getApkFile(), settings.getPlatformDirectory());
        flowDroidSetup.setupFlowDroid();
        additionalFrameworkSetup();

        DataFlowScope dataFlowScope =
                new CryptoAnalysisDataFlowScope(rules, Collections.emptySet());
        super.getAnalysisReporter().beforeCallGraphConstruction();
        FrameworkScope frameworkScope = flowDroidSetup.createFrameworkScope(dataFlowScope);
        super.getAnalysisReporter().afterCallGraphConstruction(frameworkScope.getCallGraph());

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
