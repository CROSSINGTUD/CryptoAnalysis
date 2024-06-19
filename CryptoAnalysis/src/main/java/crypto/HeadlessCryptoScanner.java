package crypto;

import boomerang.debugger.Debugger;
import boomerang.debugger.IDEVizDebugger;
import boomerang.scene.CallGraph;
import boomerang.scene.Method;
import boomerang.scene.WrappedClass;
import boomerang.scene.jimple.SootCallGraph;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import crypto.AnalysisSettings.AnalysisCallGraph;
import crypto.analysis.CryptoScanner;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.cryslhandler.RulesetReader;
import crypto.exceptions.CryptoAnalysisException;
import crypto.exceptions.CryptoAnalysisParserException;
import crypto.listener.IAnalysisListener;
import crypto.listener.IErrorListener;
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

public class HeadlessCryptoScanner {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HeadlessCryptoScanner.class);

	private final AnalysisSettings settings;
	private final Collection<IAnalysisListener> analysisListeners = new HashSet<>();
	private final Collection<IErrorListener> errorListeners = new HashSet<>();
	private final Table<WrappedClass, Method, Set<AbstractError>> errorCollection = HashBasedTable.create();

	public static int exitCode = 0;

	public HeadlessCryptoScanner(String applicationPath, String rulesetDirectory) {
		settings = new AnalysisSettings();

		settings.setApplicationPath(applicationPath);
		settings.setRulesetPath(rulesetDirectory);
		settings.setReportFormats(new HashSet<>());
	}

	private HeadlessCryptoScanner(AnalysisSettings settings) {
		this.settings = settings;
	}

	public static void main(String[] args) {
		try {
			HeadlessCryptoScanner scanner = createFromCLISettings(args);
			scanner.run();
		} catch (CryptoAnalysisParserException e) {
			throw new RuntimeException("Error while parsing the CLI arguments: " + e.getMessage());
		}
		System.exit(exitCode);
	}

	public static HeadlessCryptoScanner createFromCLISettings(String[] args) throws CryptoAnalysisParserException {
		AnalysisSettings analysisSettings = new AnalysisSettings();
		analysisSettings.parseSettingsFromCLI(args);

		return new HeadlessCryptoScanner(analysisSettings);
	}

	public void run() {
		Stopwatch stopwatch = Stopwatch.createStarted();
		LOGGER.info("Setup Soot...");
		setupSoot();
		LOGGER.info("Soot setup done in {} ", stopwatch);

		LOGGER.info("Starting analysis...");
		analyze();
		LOGGER.info("Analysis finished in {}", stopwatch);
		stopwatch.stop();
	}

	private void setupSoot() {
		try {
			initializeSootWithEntryPointAllReachable();
		} catch (CryptoAnalysisException e) {
			throw new RuntimeException("Error happened while setting up Soot: " + e.getMessage());
		}
		PackManager.v().getPack("cg").apply();
	}

	private void analyze() {
		// Create ruleset and reporter
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

			@Override
			public Debugger<TransitionFunction> debugger(IDEALSeedSolver<TransitionFunction> solver, IAnalysisSeed seed) {
				if (!isVisualization()) {
					return super.debugger(solver, seed);
				}

				if (getReportDirectory() == null) {
					LOGGER.error("The visualization requires the --reportDir option");
					return super.debugger(solver, seed);
				}

				File vizFile = new File(getReportDirectory() + File.separator + "viz" + File.separator + "ObjectId#" + seed.getObjectId() + ".json");
				boolean created = vizFile.getParentFile().mkdirs();

				if (!created) {
					LOGGER.error("Could not create directory {}", vizFile.getAbsolutePath());
					return new Debugger<>();
				}

				return new IDEVizDebugger<>(vizFile);
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

		/*if (providerDetection()) {
					ProviderDetection providerDetection = new ProviderDetection(ruleReader);

					if(rulesetRootPath == null) {
						rulesetRootPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources";
					}

					String detectedProvider = providerDetection.doAnalysis(scanner.icfg(), rulesetRootPath);

					if(detectedProvider != null) {
						rules.clear();
						switch(settings.getRulesetPathType()) {
							case DIR:
								rules.addAll(providerDetection.chooseRules(rulesetRootPath + File.separator + detectedProvider));
								break;
							case ZIP:
								rules.addAll(providerDetection.chooseRulesZip(rulesetRootPath + File.separator + detectedProvider + ".zip"));
								break;
							default:
								rules.addAll(providerDetection.chooseRules(rulesetRootPath + File.separator + detectedProvider));
						}
					}
				}*/
	}
	
	public String toString() {
		String s = "HeadlessCryptoScanner: \n";
		s += "\tSoftwareIdentifier: " + getSoftwareIdentifier() + "\n";
		s += "\tApplicationClassPath: " + getApplicationPath() + "\n";
		s += "\tSootClassPath: " + getSootClassPath() + "\n\n";
		return s;
	}

	private void initializeSootWithEntryPointAllReachable() throws CryptoAnalysisException {
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

	public void addAnalysisListener(IAnalysisListener analysisListener) {
		analysisListeners.add(analysisListener);
	}

	public void addErrorListener(IErrorListener errorListener) {
		errorListeners.add(errorListener);
	}

	public Table<WrappedClass, Method, Set<AbstractError>> getErrorCollection() {
		return errorCollection;
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
	
	public boolean isProviderDetection() {
		return settings.isProviderDetectionAnalysis();
	}

	public void setProviderDetection(boolean providerDetection) {
		settings.setProviderDetection(providerDetection);
	}

	public Collection<String> getIgnoredSections() {
		return settings.getIgnoredSections();
	}

	public void setIgnoredSections(Collection<String> ignoredSections) {
		settings.setIgnoredSections(ignoredSections);
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
