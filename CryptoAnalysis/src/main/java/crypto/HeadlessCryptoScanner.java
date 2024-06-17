package crypto;

import boomerang.debugger.Debugger;
import boomerang.debugger.IDEVizDebugger;
import boomerang.scene.CallGraph;
import boomerang.scene.Method;
import boomerang.scene.WrappedClass;
import boomerang.scene.jimple.SootCallGraph;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import crypto.analysis.AnalysisSettings;
import crypto.analysis.AnalysisSettings.ControlGraph;
import crypto.analysis.AnalysisSettings.ReportFormat;
import crypto.analysis.CrySLAnalysisListener;
import crypto.analysis.CrySLResultsReporter;
import crypto.analysis.CryptoScanner;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.cryslhandler.RulesetReader;
import crypto.exceptions.CryptoAnalysisException;
import crypto.exceptions.CryptoAnalysisParserException;
import crypto.preanalysis.SeedFactory;
import crypto.preanalysis.TransformerSetup;
import crypto.providerdetection.ProviderDetection;
import crypto.reporting.Reporter;
import crypto.reporting.ReporterFactory;
import crypto.rules.CrySLRule;
import ideal.IDEALSeedSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.Body;
import soot.BodyTransformer;
import soot.EntryPoints;
import soot.G;
import soot.PackManager;
import soot.PhaseOptions;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.Transform;
import soot.Transformer;
import soot.Unit;
import soot.options.Options;
import typestate.TransitionFunction;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class HeadlessCryptoScanner {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HeadlessCryptoScanner.class);
	private static final AnalysisSettings settings = new AnalysisSettings();
	private static Stopwatch callGraphWatch;
	private static List<CrySLRule> rules = Lists.newArrayList();
	private static String rulesetRootPath;
	private static final crypto.rules.CrySLRuleReader ruleReader = new crypto.rules.CrySLRuleReader();
	private boolean hasSeeds;

	public static int exitCode = 0;

	// public HeadlessCryptoScanner(String applicationPath, String rulesDirectory) {}

	public static void main(String[] args) {
		HeadlessCryptoScanner scanner = createFromCLISettings(args);
		scanner.exec();
		System.exit(exitCode);
	}

	public static HeadlessCryptoScanner createFromCLISettings(String[] args) {
		try {
			settings.parseSettingsFromCLI(args);
		} catch (CryptoAnalysisParserException e) {
			LOGGER.error("Parser failed with error: " + e.getClass(), e);
			System.exit(-1);
		}
		
		HeadlessCryptoScanner scanner = new HeadlessCryptoScanner() {

			@Override
			protected String applicationClassPath() {
				return settings.getApplicationPath();
			}

			@Override
			protected List<CrySLRule> getRules() {
				switch(settings.getRulesetPathType()) {
					case DIR:
						try {
							File ruleSetDir = new File(settings.getRulesetPathDir());
							rules.addAll(ruleReader.readFromDirectory(ruleSetDir));
							rulesetRootPath = ruleSetDir.getParent();
						} catch (CryptoAnalysisException e) {
							LOGGER.error("Error happened when getting the CrySL rules from the specified directory: " + settings.getRulesetPathDir(), e);
						}
						break;
					case ZIP:
						try {
							File ruleSetZip = new File(settings.getRulesetPathDir());
							rules.addAll(ruleReader.readFromZipFile(ruleSetZip));
							rulesetRootPath = ruleSetZip.getParent();
						} catch (CryptoAnalysisException e) {
							LOGGER.error("Error happened when getting the CrySL rules from the specified file: " + settings.getRulesetPathDir(), e);
						}
						break;
					default:
						LOGGER.error("Error happened when getting the CrySL rules from the specified file.");
				}
				return rules;
			}
			
		};
		return scanner;
	}

	public void exec() {
		Stopwatch stopwatch = Stopwatch.createStarted();
		if(isPreAnalysis()){
			try {
				initializeSootWithEntryPointAllReachable(false);
			} catch (CryptoAnalysisException e) {
				LOGGER.error("Error happened when executing HeadlessCryptoScanner.", e);
			}
			LOGGER.info("Pre-Analysis soot setup done in {} ", stopwatch);
			checkIfUsesObject();
			LOGGER.info("Pre-Analysis  finished in {}", stopwatch);
		}
		if (!isPreAnalysis() || hasSeeds()) {
			LOGGER.info("Using call graph algorithm {}", callGraphAlgorithm());
			try {
				initializeSootWithEntryPointAllReachable(true);
			} catch (CryptoAnalysisException e) {
				LOGGER.error("Error happened when executing HeadlessCryptoScanner.", e);
			}

			LOGGER.info("Analysis soot setup done in {} ", stopwatch);
			analyse();
			LOGGER.info("Analysis finished in {}", stopwatch);
		}
	}

	public boolean hasSeeds(){
		return hasSeeds;
	}
	
	private void checkIfUsesObject() {
		final SeedFactory seedFactory = new SeedFactory(HeadlessCryptoScanner.rules);
		PackManager.v().getPack("jap").add(new Transform("jap.myTransform", new BodyTransformer() {
			protected void internalTransform(Body body, String phase, Map<String, String> options) {
				if (!body.getMethod().getDeclaringClass().isApplicationClass()) {
					return;
				}
				for (Unit u : body.getUnits()) {
					seedFactory.generate(body.getMethod(), u);
				}
			}
		}));
		PhaseOptions.v().setPhaseOption("jap.npc", "on");
		PackManager.v().runPacks();
		hasSeeds = seedFactory.hasSeeds();
	}

	private void analyse() {
		Transform transform = new Transform("wjtp.ifds", createAnalysisTransformer());
		PackManager.v().getPack("wjtp").add(transform);
		callGraphWatch = Stopwatch.createStarted();
		
		PackManager.v().getPack("cg").apply();
		PackManager.v().getPack("wjtp").apply();
	}
	
	public String toString() {
		String s = "HeadlessCryptoScanner: \n";
		s += "\tSoftwareIdentifier: " + softwareIdentifier() + "\n";
		s += "\tApplicationClassPath: " + applicationClassPath() + "\n";
		s += "\tSootClassPath: " + sootClassPath() + "\n\n";
		return s;
	}

	private Transformer createAnalysisTransformer() {
		return new SceneTransformer() {
			
			@Override
			protected void internalTransform(String phaseName, Map<String, String> options) {

				List<CrySLRule> rules = HeadlessCryptoScanner.rules;

				final CrySLResultsReporter resultsReporter = new CrySLResultsReporter();


				if(getAdditionalListener() != null) {
					resultsReporter.addReportListener(getAdditionalListener());
				}





				// Create ruleset and reporter
				Collection<CrySLRule> ruleset;
				try {
					RulesetReader reader = new RulesetReader();
					ruleset = reader.readRulesFromPath(getRulesetPath());
				} catch (IOException e) {
					throw new RuntimeException("Could not read rules: " + e.getMessage());
				}

				Collection<Reporter> reporters = ReporterFactory.createReporters(getReportFormats(), getOutputDirectory(), ruleset);

				// Initialize scanner
				TransformerSetup.v().setupPreTransformer(ruleset);
				CallGraph callGraph = new SootCallGraph();
				
				CryptoScanner scanner = new CryptoScanner(ruleset) {

					@Override
					public CallGraph callGraph() {
						return callGraph;
					}

					@Override
					public CrySLResultsReporter getAnalysisListener() {
						return resultsReporter;
					}
					
					@Override
					public Debugger<TransitionFunction> debugger(IDEALSeedSolver<TransitionFunction> solver, IAnalysisSeed seed) {
						if (!isVisualization()) {
							return super.debugger(solver, seed);
						}

						if (getOutputDirectory() == null) {
							LOGGER.error("The visualization requires the --reportDir option");
							return super.debugger(solver, seed);
						}

						File vizFile = new File(getOutputDirectory() + File.separator + "viz" + File.separator + "ObjectId#" + seed.getObjectId() + ".json");
						boolean created = vizFile.getParentFile().mkdirs();

						if (!created) {
							LOGGER.error("Could not create directory {}", vizFile.getAbsolutePath());
							return new Debugger<>();
						}
							
						return new IDEVizDebugger<>(vizFile);
					}

				};



				
				if (providerDetection()) {
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
				}




				scanner.scan();

				// Report the findings
				Collection<IAnalysisSeed> discoveredSeeds = scanner.getDiscoveredSeeds();
				Table<WrappedClass, Method, Set<AbstractError>> errorCollection = scanner.getErrorCollector().getErrorCollection();

				for (Reporter reporter : reporters) {
					reporter.createAnalysisReport(discoveredSeeds, errorCollection);
				}
			}
		};
	}

	protected CrySLAnalysisListener getAdditionalListener() {
		return null;
	}

	private void initializeSootWithEntryPointAllReachable(boolean wholeProgram) throws CryptoAnalysisException {
		G.reset();
		Options.v().set_whole_program(wholeProgram);
		switch (callGraphAlgorithm()) {
		case CHA:
			Options.v().setPhaseOption("cg.cha", "on");
			break;
		case SPARKLIB:
			Options.v().setPhaseOption("cg.spark", "on");
			Options.v().setPhaseOption("cg", "library:any-subtype");
			break;
		case SPARK:
			Options.v().setPhaseOption("cg.spark", "on");
			break;
		default:
			throw new CryptoAnalysisException("No call graph option selected out of: CHA, SPARK_LIBRARY and SPARK");
		}
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

		// JAVA 8
		if(getJavaVersion() < 9)
		{
			Options.v().set_prepend_classpath(true);
			Options.v().set_soot_classpath(sootClassPath()+ File.pathSeparator + pathToJCE());
		}
		// JAVA VERSION 9 && IS A CLASSPATH PROJECT
		else if(getJavaVersion() >= 9 && !isModularProject())
		{
			Options.v().set_soot_classpath("VIRTUAL_FS_FOR_JDK" + File.pathSeparator + sootClassPath());
		}
		// JAVA VERSION 9 && IS A MODULEPATH PROJECT
		else if(getJavaVersion() >= 9 && isModularProject())
		{
			Options.v().set_prepend_classpath(true);
			Options.v().set_soot_modulepath(sootClassPath());
		}
		Options.v().set_process_dir(Arrays.asList(applicationClassPath().split(File.pathSeparator)));
		Options.v().set_include(getIncludeList());
		Options.v().set_exclude(getExcludeList());
		Options.v().set_full_resolver(true);
		Scene.v().loadNecessaryClasses();
		Scene.v().setEntryPoints(getEntryPoints());
	}

	private List<SootMethod> getEntryPoints() {
		List<SootMethod> entryPoints = Lists.newArrayList();
		entryPoints.addAll(EntryPoints.v().application());
		entryPoints.addAll(EntryPoints.v().methodsOfApplicationClasses());
		return entryPoints;
	}
	
	private List<String> getIncludeList() {
		List<String> includeList = new LinkedList<String>();
		includeList.add("java.lang.AbstractStringBuilder");
		includeList.add("java.lang.Boolean");
		includeList.add("java.lang.Byte");
		includeList.add("java.lang.Class");
		includeList.add("java.lang.Integer");
		includeList.add("java.lang.Long");
		includeList.add("java.lang.Object");
		includeList.add("java.lang.String");
		includeList.add("java.lang.StringCoding");
		includeList.add("java.lang.StringIndexOutOfBoundsException");
		return new LinkedList<>();
	}

	private List<String> getExcludeList() {
		List<String> exList = new LinkedList<String>();
		List<CrySLRule> rules = getRules();

		for (CrySLRule r : rules) {
			exList.add(r.getClassName());
		}
		return exList;
	}
	
	protected abstract List<CrySLRule> getRules();
	
	// used to set the rules when they are loaded from headless
	// tests and not from CLI
	public static void setRules(List<CrySLRule> rules) {
		HeadlessCryptoScanner.rules = rules;
	}

	protected abstract String applicationClassPath();

	protected ControlGraph callGraphAlgorithm() {
		return settings.getControlGraph();
	}

	protected String sootClassPath() {
		return settings.getSootPath();
	}
	
	protected String softwareIdentifier(){
		return settings.getIdentifier();
	}
	
	protected String getOutputDirectory(){
		return settings.getReportDirectory();
	}

	public String getRulesetPath() {
		return settings.getRulesetPathDir();
	}
	
	protected boolean isPreAnalysis() {
		return settings.isPreAnalysis();
	}

	protected boolean isVisualization(){
		return settings.isVisualization();
	}
	
	protected Set<ReportFormat> getReportFormats() {
		return settings.getReportFormats();
	}
	
	protected boolean providerDetection() {
		return settings.isProviderDetectionAnalysis();
	}
	
	protected boolean includeStatistics() {
		return settings.isIncludeStatistics();
	}

	protected Collection<String> ignoredSections() {
		return settings.getIgnoredSections();
	}

	private static String pathToJCE() {
		// When whole program mode is disabled, the classpath misses jce.jar
		return System.getProperty("java.home") + File.separator + "lib" + File.separator + "jce.jar";
	}
	
	private static int getJavaVersion() {
	    String version = System.getProperty("java.version");
	    if(version.startsWith("1.")) {
	        version = version.substring(2, 3);
	    } else {
	        int dot = version.indexOf(".");
	        if(dot != -1) { version = version.substring(0, dot); }
	    } return Integer.parseInt(version);
	}
	
	private boolean isModularProject() {
		String applicationClassPath = applicationClassPath();
		File dirName = new File(applicationClassPath);
	    String moduleFile = dirName + File.separator + "module-info.class";
	    boolean check = new File(moduleFile).exists();
	    return check;
	}
}
