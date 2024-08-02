package de.fraunhofer.iem.scanner;

import boomerang.debugger.Debugger;
import boomerang.debugger.IDEVizDebugger;
import boomerang.scene.CallGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.Method;
import boomerang.scene.WrappedClass;
import boomerang.scene.jimple.SootCallGraph;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import de.fraunhofer.iem.scanner.AnalysisSettings.AnalysisCallGraph;
import crypto.analysis.CryptoAnalysisDataFlowScope;
import crypto.analysis.CryptoScanner;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.ScannerDefinition;
import crypto.analysis.errors.AbstractError;
import crypto.cryslhandler.RulesetReader;
import crypto.exceptions.CryptoAnalysisException;
import crypto.exceptions.CryptoAnalysisParserException;
import crypto.preanalysis.TransformerSetup;
import crypto.reporting.Reporter;
import crypto.reporting.ReporterFactory;
import crypto.rules.CrySLRule;
import ideal.IDEALSeedSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.EntryPoints;
import soot.G;
import soot.PackManager;
import soot.Scene;
import soot.SootMethod;
import soot.options.Options;
import typestate.TransitionFunction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HeadlessJavaScanner {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HeadlessJavaScanner.class);

	private final AnalysisSettings settings;
	private final CryptoScanner scanner;

	public HeadlessJavaScanner(String applicationPath, String rulesetDirectory) {
		settings = new AnalysisSettings();

		settings.setApplicationPath(applicationPath);
		settings.setRulesetPath(rulesetDirectory);
		settings.setReportFormats(new HashSet<>());

		scanner = new CryptoScanner(createScannerDefinition());
	}

	private HeadlessJavaScanner(AnalysisSettings settings) {
		this.settings = settings;

		scanner = new CryptoScanner(createScannerDefinition());
	}

	public static void main(String[] args) {
		try {
			HeadlessJavaScanner scanner = createFromCLISettings(args);
			scanner.run();
		} catch (CryptoAnalysisParserException e) {
			throw new RuntimeException("Error while parsing the CLI arguments: " + e.getMessage());
		}
	}

	public static HeadlessJavaScanner createFromCLISettings(String[] args) throws CryptoAnalysisParserException {
		AnalysisSettings analysisSettings = new AnalysisSettings();
		analysisSettings.parseSettingsFromCLI(args);

		return new HeadlessJavaScanner(analysisSettings);
	}

	public ScannerDefinition createScannerDefinition() {
		return new ScannerDefinition() {

			@Override
			public CallGraph constructCallGraph(Collection<CrySLRule> ruleset) {
				TransformerSetup.v().setupPreTransformer(ruleset);

				return new SootCallGraph();
			}

			@Override
			public Collection<CrySLRule> readRuleset() {
				LOGGER.info("Reading rules from {}", getRulesetDirectory());
				Collection<CrySLRule> ruleset;
				try {
					RulesetReader reader = new RulesetReader();
					ruleset = reader.readRulesFromPath(getRulesetDirectory());
				} catch (IOException e) {
					throw new CryptoAnalysisException("Could not read rules: " + e.getMessage());
				}
				LOGGER.info("Found {} rules in {}", ruleset.size(), getRulesetDirectory());

				return ruleset;
			}

			@Override
			public DataFlowScope createDataFlowScope(Collection<CrySLRule> ruleset) {
				return new CryptoAnalysisDataFlowScope(ruleset, getIgnoredSections());
			}

			@Override
			public Debugger<TransitionFunction> debugger(IDEALSeedSolver<TransitionFunction> seedSolver) {
				if (!isVisualization()) {
					return new Debugger<>();
				}

				if (getReportDirectory() == null) {
					LOGGER.error("The visualization requires the --reportDir option. Disabling visualization...");
					return new Debugger<>();
				}

				File vizFile = new File(getReportDirectory() + File.separator + "viz" + File.separator + "ObjectId#" + ".json");
				boolean created = vizFile.getParentFile().mkdirs();

				if (!created) {
					LOGGER.error("Could not create directory {}. Disabling visualization...", vizFile.getAbsolutePath());
					return new Debugger<>();
				}

				return new IDEVizDebugger<>(vizFile);
			}

			@Override
			public int timeout() {
				return getTimeout();
			}
		};
	}

	public void run() {
		Stopwatch stopwatch = Stopwatch.createStarted();
		LOGGER.info("Setup Soot...");
		initializeSootWithEntryPointAllReachable();
		PackManager.v().getPack("cg").apply();
		LOGGER.info("Soot setup done in {} ", stopwatch);

		LOGGER.info("Starting analysis...");
		analyze();
		LOGGER.info("Analysis finished in {}", stopwatch);
		stopwatch.stop();
	}

	private void analyze() {
		// Run scanner
		scanner.scan();

		// Report the findings
		Collection<CrySLRule> ruleset = scanner.getRuleset();
		Collection<IAnalysisSeed> discoveredSeeds = scanner.getDiscoveredSeeds();
		Table<WrappedClass, Method, Set<AbstractError>> errors = scanner.getCollectedErrors();
		Collection<Reporter> reporters = ReporterFactory.createReporters(getReportFormats(), getReportDirectory(), ruleset);

		for (Reporter reporter : reporters) {
			reporter.createAnalysisReport(discoveredSeeds, errors);
		}
	}
	
	public String toString() {
		String s = "HeadlessCryptoScanner: \n";
		s += "\tSoftwareIdentifier: " + getSoftwareIdentifier() + "\n";
		s += "\tApplicationClassPath: " + getApplicationPath() + "\n";
		s += "\tSootClassPath: " + getSootClassPath() + "\n\n";
		return s;
	}

	private void initializeSootWithEntryPointAllReachable() {
		G.reset();
		Options.v().set_whole_program(true);

		switch (getCallGraphAlgorithm()) {
			case CHA:
				Options.v().setPhaseOption("cg.cha", "on");
				break;
			case SPARK_LIB:
				Options.v().setPhaseOption("cg.spark", "on");
				Options.v().setPhaseOption("cg", "library:any-subtype");
				break;
			case SPARK:
				Options.v().setPhaseOption("cg.spark", "on");
				break;
			default:
				throw new CryptoAnalysisException("No call graph option selected out of: CHA, SPARK_LIB and SPARK");
		}
		LOGGER.info("Using call graph algorithm: {}", getCallGraphAlgorithm());

		Options.v().set_output_format(Options.output_format_none);
		Options.v().set_no_bodies_for_excluded(true);
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_keep_line_number(true);

		/* This phase is new in soot 4.3.0 and manipulates the jimple code in a
		 * way that CryptoAnalysis is not able to find seeds in some cases (see
		 * https://github.com/CROSSINGTUD/CryptoAnalysis/issues/293). Therefore,
		 * it is disabled.
		 */
		Options.v().setPhaseOption("jb.sils", "enabled:false");
		// Options.v().setPhaseOption("jb", "use-original-names:true");

		// JAVA 8
		if (getJavaVersion() < 9) {
			Options.v().set_prepend_classpath(true);
			Options.v().set_soot_classpath(getSootClassPath() + File.pathSeparator + pathToJCE());
		}
		// JAVA VERSION 9 && IS A CLASSPATH PROJECT
		else if(getJavaVersion() >= 9 && !isModularProject()) {
			Options.v().set_soot_classpath("VIRTUAL_FS_FOR_JDK" + File.pathSeparator + getSootClassPath());
		}
		// JAVA VERSION 9 && IS A MODULEPATH PROJECT
		else if(getJavaVersion() >= 9 && isModularProject()) {
			Options.v().set_prepend_classpath(true);
			Options.v().set_soot_modulepath(getSootClassPath());
		}

		Options.v().set_process_dir(Arrays.asList(settings.getApplicationPath().split(File.pathSeparator)));
		Options.v().set_include(new ArrayList<>());
		Options.v().set_exclude(new ArrayList<>());
		Options.v().set_full_resolver(true);
		Scene.v().loadNecessaryClasses();
		Scene.v().setEntryPoints(getEntryPoints());

		additionalSootSetup();
	}

	private List<SootMethod> getEntryPoints() {
		List<SootMethod> entryPoints = Lists.newArrayList();

		entryPoints.addAll(EntryPoints.v().application());
		entryPoints.addAll(EntryPoints.v().methodsOfApplicationClasses());

		return entryPoints;
	}

	public void additionalSootSetup() {}

	public CryptoScanner getScanner() {
		return scanner;
	}

	public Table<WrappedClass, Method, Set<AbstractError>> getCollectedErrors() {
		return scanner.getCollectedErrors();
	}

	public AnalysisCallGraph getCallGraphAlgorithm() {
		return settings.getCallGraph();
	}

	public void setCallGraphAlgorithm(AnalysisCallGraph analysisCallGraph) {
		settings.setCallGraph(analysisCallGraph);
	}

	public String getSootClassPath() {
		return settings.getSootPath();
	}

	public void setSootClassPath(String sootClassPath) {
		settings.setSootPath(sootClassPath);
	}
	
	public String getSoftwareIdentifier(){
		return settings.getIdentifier();
	}

	public void setSoftwareIdentifier(String softwareIdentifier) {
		settings.setIdentifier(softwareIdentifier);
	}
	
	public String getReportDirectory(){
		return settings.getReportDirectory();
	}

	public void setReportDirectory(String reportDirectory) {
		settings.setReportDirectory(reportDirectory);
	}

	public String getApplicationPath() {
		return settings.getApplicationPath();
	}

	public String getRulesetDirectory() {
		return settings.getRulesetPath();
	}

	public boolean isVisualization(){
		return settings.isVisualization();
	}

	public void setVisualization(boolean visualization) {
		settings.setVisualization(visualization);
	}
	
	public Set<Reporter.ReportFormat> getReportFormats() {
		return settings.getReportFormats();
	}

	public void setReportFormats(Reporter.ReportFormat... formats) {
		setReportFormats(Arrays.asList(formats));
	}

	public void setReportFormats(Collection<Reporter.ReportFormat> reportFormats) {
		settings.setReportFormats(reportFormats);
	}

	public Collection<String> getIgnoredSections() {
		return settings.getIgnoredSections();
	}

	public void setIgnoredSections(Collection<String> ignoredSections) {
		settings.setIgnoredSections(ignoredSections);
	}

	public int getTimeout() {
		return settings.getTimeout();
	}

	public void setTimeout(int timeout) {
		settings.setTimeout(timeout);
	}

	private static String pathToJCE() {
		// When whole program mode is disabled, the classpath misses jce.jar
		return System.getProperty("java.home") + File.separator + "lib" + File.separator + "jce.jar";
	}
	
	private static int getJavaVersion() {
	    String version = System.getProperty("java.version");
	    if (version.startsWith("1.")) {
	        version = version.substring(2, 3);
	    } else {
	        int dot = version.indexOf(".");
	        if (dot != -1) {
				version = version.substring(0, dot);
			}
	    }
		return Integer.parseInt(version);
	}
	
	private boolean isModularProject() {
		String applicationClassPath = settings.getApplicationPath();
		File dirName = new File(applicationClassPath);
		String moduleFile = dirName + File.separator + "module-info.class";
		return new File(moduleFile).exists();
	}
}
