package de.fraunhofer.iem.crypto;

import boomerang.scene.CallGraph;
import boomerang.scene.Method;
import boomerang.scene.WrappedClass;
import boomerang.scene.jimple.SootCallGraph;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import crypto.analysis.CryptoScanner;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.cryslhandler.RulesetReader;
import crypto.exceptions.CryptoAnalysisParserException;
import crypto.listener.IAnalysisListener;
import crypto.listener.IErrorListener;
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

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class HeadlessAndroidScanner {

	private static final Logger LOGGER = LoggerFactory.getLogger(HeadlessAndroidScanner.class);

	private final AndroidSettings settings;
	private final Collection<IAnalysisListener> analysisListeners = new HashSet<>();
	private final Collection<IErrorListener> errorListeners = new HashSet<>();
	private final Table<WrappedClass, Method, Set<AbstractError>> errorCollection = HashBasedTable.create();

	public HeadlessAndroidScanner(String apkFile, String platformsDirectory, String rulesetDirectory) {
		settings = new AndroidSettings();
		settings.setApkFile(apkFile);
		settings.setPlatformDirectory(platformsDirectory);
		settings.setRulesetDirectory(rulesetDirectory);
	}

	private HeadlessAndroidScanner(AndroidSettings settings) {
		this.settings = settings;
	}

	public static void main(String[] args) {
		try {
			HeadlessAndroidScanner scanner = createFromCLISettings(args);
			scanner.run();
		} catch (CryptoAnalysisParserException e) {
			throw new RuntimeException("Error while parsing the CLI arguments: " + e.getMessage());
		}
	}

	public static HeadlessAndroidScanner createFromCLISettings(String[] args) throws CryptoAnalysisParserException {
		AndroidSettings androidSettings = new AndroidSettings();
		androidSettings.parseSettingsFromCLI(args);

		return new HeadlessAndroidScanner(androidSettings);
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
		config.getCallbackConfig().setEnableCallbacks(false);
		config.setCodeEliminationMode(InfoflowConfiguration.CodeEliminationMode.NoCodeElimination);
		config.getAnalysisFileConfig().setAndroidPlatformDir(getPlatformDirectory());
		config.getAnalysisFileConfig().setTargetAPKFile(getApkFile());
		config.setMergeDexFiles(true);
		SetupApplication flowDroid = new SetupApplication(config);
		SootConfigForAndroid sootConfigForAndroid = new SootConfigForAndroid() {
			@Override
			public void setSootOptions(Options options, InfoflowConfiguration config) {
				options.set_keep_line_number(true);
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

		LOGGER.info("Reading rules from {}", getRulesetDirectory());
		Collection<CrySLRule> ruleset;
		try {
			RulesetReader reader = new RulesetReader();
			ruleset = reader.readRulesFromPath(getRulesetDirectory());
		} catch (IOException e) {
			throw new RuntimeException("Could not read rules: " + e.getMessage());
		}
		LOGGER.info("Found {} rules in {}", ruleset.size(), getRulesetDirectory());

		Collection<Reporter> reporters = ReporterFactory.createReporters(getReportFormats(), getReportDirectory(), ruleset);

		// Prepare for Boomerang
		TransformerSetup.v().setupPreTransformer(ruleset);
		CallGraph callGraph = new SootCallGraph();

		// Initialize scanner
		CryptoScanner scanner = new CryptoScanner(ruleset) {
			@Override
			public CallGraph callGraph() {
				return callGraph;
			}
		};

		for (IAnalysisListener analysisListener : analysisListeners) {
			scanner.addAnalysisListener(analysisListener);
		}

		for (IErrorListener errorListener : errorListeners) {
			scanner.addErrorListener(errorListener);
		}

		// Run scanner
		scanner.scan();

		// Report the findings
		Collection<IAnalysisSeed> discoveredSeeds = scanner.getDiscoveredSeeds();
		Table<WrappedClass, Method, Set<AbstractError>> errors = scanner.getCollectedErrors();
		errorCollection.putAll(errors);

		for (Reporter reporter : reporters) {
			reporter.createAnalysisReport(discoveredSeeds, errors);
		}
	}

	public void addAnalysisListener(IAnalysisListener analysisListener) {
		analysisListeners.add(analysisListener);
	}

	public void addErrorListener(IErrorListener errorListener) {
		errorListeners.add(errorListener);
	}

	public Table<WrappedClass, Method, Set<AbstractError>> getErrorCollection() {
		return errorCollection;
	}

	public String getApkFile(){
		return settings.getApkFile();
	}

	public String getPlatformDirectory(){
		return settings.getPlatformDirectory();
	}

	public String getRulesetDirectory(){
		return settings.getRulesetDirectory();
	}

	public Collection<Reporter.ReportFormat> getReportFormats() {
		return settings.getReportFormats();
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
