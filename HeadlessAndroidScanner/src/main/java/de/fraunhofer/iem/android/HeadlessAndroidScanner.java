package de.fraunhofer.iem.android;

import boomerang.scene.CallGraph;
import boomerang.scene.jimple.SootCallGraph;
import com.google.common.base.Stopwatch;
import crypto.analysis.CryptoScanner;
import crypto.exceptions.CryptoAnalysisParserException;
import crypto.preanalysis.TransformerSetup;
import crypto.reporting.Reporter;
import crypto.reporting.ReporterFactory;
import crypto.rules.CrySLRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.config.SootConfigForAndroid;
import soot.options.Options;

import java.util.Arrays;
import java.util.Collection;

public class HeadlessAndroidScanner extends CryptoScanner {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeadlessAndroidScanner.class);

    private final AndroidSettings settings;

    public HeadlessAndroidScanner(String apkFile, String platformsDirectory, String rulesetDirectory) {
        settings = new AndroidSettings();

        settings.setApkFile(apkFile);
        settings.setPlatformDirectory(platformsDirectory);
        settings.setRulesetDirectory(rulesetDirectory);
    }

    private HeadlessAndroidScanner(AndroidSettings settings) {
        this.settings = settings;
    }

    public static HeadlessAndroidScanner createFromCLISettings(String[] args) throws CryptoAnalysisParserException {
        AndroidSettings androidSettings = new AndroidSettings();
        androidSettings.parseSettingsFromCLI(args);

        return new HeadlessAndroidScanner(androidSettings);
    }

    @Override
    public String getRulesetPath() {
        return settings.getRulesetDirectory();
    }

    @Override
    public CallGraph constructCallGraph(Collection<CrySLRule> ruleset) {
        TransformerSetup.v().setupPreTransformer(ruleset);

        return new SootCallGraph();
    }

    public void run() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        LOGGER.info("Setup Soot and FlowDroid...");
        constructCallGraph();
        LOGGER.info("Soot setup done in {} ", stopwatch);

        LOGGER.info("Starting analysis...");
        runCryptoAnalysis();
        LOGGER.info("Analysis finished in {}", stopwatch);
        stopwatch.stop();
    }

    private void constructCallGraph() {
        InfoflowAndroidConfiguration config = new InfoflowAndroidConfiguration();
        config.setCallgraphAlgorithm(InfoflowConfiguration.CallgraphAlgorithm.CHA);
        config.setCodeEliminationMode(InfoflowConfiguration.CodeEliminationMode.NoCodeElimination);
        config.getAnalysisFileConfig().setAndroidPlatformDir(getPlatformDirectory());
        config.getAnalysisFileConfig().setTargetAPKFile(getApkFile());
        config.setMergeDexFiles(true);
        config.setTaintAnalysisEnabled(false);
        config.setIgnoreFlowsInSystemPackages(true);
        config.setExcludeSootLibraryClasses(true);

        SetupApplication flowDroid = new SetupApplication(config);
        SootConfigForAndroid sootConfigForAndroid = new SootConfigForAndroid() {
            @Override
            public void setSootOptions(Options options, InfoflowConfiguration config) {
                options.set_keep_line_number(true);
                options.setPhaseOption("jb.sils", "enabled:false");
            }
        };
        flowDroid.setSootConfig(sootConfigForAndroid);
        LOGGER.info("Constructing call graph");
        flowDroid.constructCallgraph();
        LOGGER.info("Done constructing call graph");
    }

    private void runCryptoAnalysis() {
        LOGGER.info("Running static analysis on APK file " + getApkFile());
        LOGGER.info("with Android Platforms dir " + getPlatformDirectory());

        // Run scanner
        super.initialize();
        super.scan();

        // Report the findings
        Collection<Reporter> reporters = ReporterFactory.createReporters(getReportFormats(), getReportDirectory(), super.getRuleset());

        for (Reporter reporter : reporters) {
            reporter.createAnalysisReport(super.getDiscoveredSeeds(), super.getCollectedErrors(), super.getStatistics());
        }
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

}
