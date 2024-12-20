package de.fraunhofer.iem.android;

import boomerang.scene.CallGraph;
import boomerang.scene.DataFlowScope;
import crypto.analysis.CryptoScanner;
import crypto.exceptions.CryptoAnalysisParserException;
import crypto.reporting.Reporter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class HeadlessAndroidScanner extends CryptoScanner {

    private final AndroidSettings settings;
    private FlowDroidSetup flowDroidSetup;

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

    @Override
    public String getRulesetPath() {
        return settings.getRulesetDirectory();
    }

    @Override
    public CallGraph constructCallGraph() {
        return flowDroidSetup.constructCallGraph(super.getRuleset());
    }

    @Override
    public DataFlowScope createDataFlowScope() {
        return new AndroidDataFlowScope(super.getRuleset(), Collections.emptySet());
    }

    public void run() {
        flowDroidSetup = new FlowDroidSetup(settings.getApkFile(), settings.getPlatformDirectory());
        flowDroidSetup.setupFlowDroid();
        additionalFrameworkSetup();

        // Initialize fields
        super.initialize();

        // Run the analysis
        super.scan();

        // Report the errors
        super.createReports(getReportFormats(), getReportDirectory(), isVisualization());
    }

    public String getApkFile() {
        return settings.getApkFile();
    }

    public String getPlatformDirectory() {
        return settings.getPlatformDirectory();
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
