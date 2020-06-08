package crypto;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import boomerang.callgraph.ObservableDynamicICFG;
import boomerang.callgraph.ObservableICFG;
import boomerang.debugger.Debugger;
import boomerang.debugger.IDEVizDebugger;
import boomerang.preanalysis.BoomerangPretransformer;
import crypto.analysis.CrySLAnalysisListener;
import crypto.analysis.CrySLResultsReporter;
import crypto.analysis.CrySLRulesetSelector;
import crypto.analysis.CrySLRulesetSelector.RuleFormat;
import crypto.analysis.CrySLRulesetSelector.Ruleset;
import crypto.analysis.CryptoScanner;
import crypto.analysis.IAnalysisSeed;
import crypto.exceptions.CryptoAnalysisException;
import crypto.preanalysis.SeedFactory;
import crypto.providerdetection.ProviderDetection;
import crypto.reporting.CSVReporter;
import crypto.reporting.CommandLineReporter;
import crypto.reporting.ErrorMarkerListener;
import crypto.reporting.SARIFReporter;
import crypto.rules.CrySLRule;
import crypto.rules.CrySLRuleReader;
import ideal.IDEALSeedSolver;
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

public abstract class HeadlessCryptoScanner {
	private boolean hasSeeds;
	private static Stopwatch callGraphWatch;
	private static CommandLine options;
	private static boolean PRE_ANALYSIS = false;
	private static List<CrySLRule> rules = Lists.newArrayList();
	private static String rootRulesDirForProvider;
	private static final Logger LOGGER = LoggerFactory.getLogger(HeadlessCryptoScanner.class);

	public static enum CG {
		CHA, SPARK_LIBRARY, SPARK
	}

	public static void main(String... args) {
		HeadlessCryptoScanner scanner;
		try {
			scanner = createFromOptions(args);
		} catch (CryptoAnalysisException e) {
			LOGGER.error("Analysis failed with error: " + e.getClass().toString(), e);
			return;
		}
		scanner.exec();
	}

	public static HeadlessCryptoScanner createFromOptions(String... args) throws CryptoAnalysisException {
		CommandLineParser parser = new DefaultParser();
		try {
			options = parser.parse(new HeadlessCryptoScannerOptions(), args);
		} catch (ParseException e) {
			commandLineParserErrorMessage(e);
			throw new CryptoAnalysisException("", e);
		}

		// TODO: Somehow optimize the rule getting because this has many code duplicates for no reason.
		String resourcesPath = "No rules location given!";
		if (options.hasOption("rulesDir")) {
			resourcesPath = options.getOptionValue("rulesDir");
			try {
				rules.addAll(CrySLRuleReader.readFromDirectory(new File(resourcesPath)));
			} catch (CryptoAnalysisException e) {
				LOGGER.error("Error happened when getting the CrySL rules from the specified directory: " + resourcesPath, e);
				throw e;
			}
			rootRulesDirForProvider = resourcesPath.substring(0, resourcesPath.lastIndexOf(File.separator));
		}
		if(options.hasOption("rulesZip")) {
			resourcesPath = options.getOptionValue("rulesZip");
			try {
				rules.addAll(CrySLRuleReader.readFromZipFile(new File(resourcesPath)));
			} catch (CryptoAnalysisException e) {
				LOGGER.error("Error happened when getting the CrySL rules from the specified file: "+resourcesPath, e);
				throw e;
			}
		}

		if (rules.isEmpty()) {
			throw new CryptoAnalysisException("No CrySL rules found: " + resourcesPath);
		}
		
		PRE_ANALYSIS = options.hasOption("preanalysis");
		final CG callGraphAlogrithm;
		if (options.hasOption("cg")) {
			String val = options.getOptionValue("cg");
			if (val.equalsIgnoreCase("spark")) {
				callGraphAlogrithm = CG.SPARK;
			} else if (val.equalsIgnoreCase("spark-library")) {
				callGraphAlogrithm = CG.SPARK_LIBRARY;
			} else {
				callGraphAlogrithm = CG.CHA;
			}
		} else {
			callGraphAlogrithm = CG.CHA;
		}
		HeadlessCryptoScanner sourceCryptoScanner = new HeadlessCryptoScanner() {

			@Override
			protected String sootClassPath() {
				return options.hasOption("sootCp") ? options.getOptionValue("sootCp") : "";
			}

			@Override
			protected String applicationClassPath() {
				return options.getOptionValue("applicationCp");
			}

			@Override
			protected CG callGraphAlogrithm() {
				return callGraphAlogrithm;
			}

			@Override
			protected String softwareIdentifier() {
				return options.getOptionValue("softwareIdentifier");
			}

			@Override
			protected String getOutputFolder(){
				return options.getOptionValue("reportDir");
			}

			@Override
			protected String getCSVOutputFile(){
				return options.getOptionValue("csvReportFile");
			}
			
			@Override
			protected boolean enableVisualization(){
				return options.hasOption("visualization");
			}
	
			@Override
			protected boolean sarifReport() {
				return options.hasOption("sarifReport");
			}
			
			@Override
			protected boolean providerDetection() {
				return options.hasOption("providerDetection");
			}
			
		};
		return sourceCryptoScanner;
	}
	

	protected String getCSVOutputFile(){
		return null;
	}


	public void exec() {
		Stopwatch stopwatch = Stopwatch.createStarted();
		if(PRE_ANALYSIS){
			try {
				initializeSootWithEntryPointAllReachable(false);
			} catch (CryptoAnalysisException e) {
				LOGGER.error("Error happened when executing HeadlessCryptoScanner.", e);
			}
			LOGGER.info("Pre-Analysis soot setup done in {} ",stopwatch);
			checkIfUsesObject();
			LOGGER.info("Pre-Analysis  finished in {}", stopwatch);
		}
		if (!PRE_ANALYSIS || hasSeeds()) {
			LOGGER.info("Using call graph algorithm {}", callGraphAlogrithm());
			try {
				initializeSootWithEntryPointAllReachable(true);
			} catch (CryptoAnalysisException e) {
				LOGGER.error("Error happened when executing HeadlessCryptoScanner.", e);
			}
			LOGGER.info("Analysis soot setup done in {} ",stopwatch);
			analyse();
			LOGGER.info("Analysis finished in {}", stopwatch);
		}
	}

	public boolean hasSeeds(){
		return hasSeeds;
	}
	private void checkIfUsesObject() {
		final SeedFactory seedFactory = new SeedFactory(getRules());
		PackManager.v().getPack("jap").add(new Transform("jap.myTransform", new BodyTransformer() {
			protected void internalTransform(Body body, String phase, Map options) {
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
		String s = "HeadllessCryptoScanner: \n";
		s += "\tSoftwareIdentifier: "+ softwareIdentifier() +"\n";
		s += "\tApplicationClassPath: "+ applicationClassPath() +"\n";
		s += "\tSootClassPath: "+ sootClassPath() +"\n\n";
		return s;
	}

	private Transformer createAnalysisTransformer() {
		return new SceneTransformer() {

			@Override
			protected void internalTransform(String phaseName, Map<String, String> options) {
				BoomerangPretransformer.v().reset();
				BoomerangPretransformer.v().apply();
				ObservableDynamicICFG observableDynamicICFG = new ObservableDynamicICFG(false);
				List<CrySLRule> rules = HeadlessCryptoScanner.this.getRules();
				ErrorMarkerListener fileReporter;
				if (sarifReport()) {
					fileReporter = new SARIFReporter(getOutputFolder(), rules);
				} else {
					fileReporter = new CommandLineReporter(getOutputFolder(), rules);
				}

				final CrySLResultsReporter reporter = new CrySLResultsReporter();
				if(getAdditionalListener() != null)
					reporter.addReportListener(getAdditionalListener());
				CryptoScanner scanner = new CryptoScanner() {

					@Override
					public ObservableICFG<Unit, SootMethod> icfg() {
						return observableDynamicICFG;
					}

					@Override
					public CrySLResultsReporter getAnalysisListener() {
						return reporter;
					}
					
					@Override
					public Debugger<TransitionFunction> debugger(IDEALSeedSolver<TransitionFunction> solver, IAnalysisSeed seed) {
						if(enableVisualization()) {
							if(getOutputFolder() == null) {
								LOGGER.error("The visualization requires the --reportDir option.");
							}
							File vizFile = new File(getOutputFolder()+"/viz/ObjectId#"+seed.getObjectId()+".json");
							vizFile.getParentFile().mkdirs();
							return new IDEVizDebugger<>(vizFile, icfg());
						}
						return super.debugger(solver, seed);
					}
				};
				
				reporter.addReportListener(fileReporter);
				String csvOutputFile = getCSVOutputFile();
				if(csvOutputFile != null){
					reporter.addReportListener(new CSVReporter(csvOutputFile,softwareIdentifier(),rules,callGraphWatch.elapsed(TimeUnit.MILLISECONDS)));
				}
				
				if (providerDetection()) {
					//create a new object to execute the Provider Detection analysis
					ProviderDetection providerDetection = new ProviderDetection();

					if(rootRulesDirForProvider == null) {
						rootRulesDirForProvider = System.getProperty("user.dir")+File.separator+"src"+File.separator+"main"+File.separator+"resources";
					}
					String detectedProvider = providerDetection.doAnalysis(observableDynamicICFG, rootRulesDirForProvider);
					if(detectedProvider != null) {
						rules.clear();
						rules.addAll(providerDetection.chooseRules(rootRulesDirForProvider+File.separator+detectedProvider));
					}
				}
				
				scanner.scan(rules);
			}
		};
	}

	protected CrySLAnalysisListener getAdditionalListener() {
		return null;
	}

	protected List<CrySLRule> getRules() {
		if (rules != null) {
			return rules;
		} else {
			try {
				return rules = CrySLRulesetSelector.makeFromRuleset("src/main/resources/JavaCryptographicArchitecture", RuleFormat.SOURCE, Ruleset.JavaCryptographicArchitecture);
			} catch (CryptoAnalysisException e) {
				LOGGER.error("Error happened when getting the CrySL rules from the specified directory: src/main/resources/JavaCryptographicArchitecture", e);
			}
		}
		return Collections.emptyList();
	}

	private void initializeSootWithEntryPointAllReachable(boolean wholeProgram) throws CryptoAnalysisException {
		G.v().reset();
		Options.v().set_whole_program(wholeProgram);

		switch (callGraphAlogrithm()) {
		case CHA:
			Options.v().setPhaseOption("cg.cha", "on");
			break;
		case SPARK_LIBRARY:
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

	private List<String> getExcludeList() {
		List<String> exList = new LinkedList<String>();
		List<CrySLRule> rules = getRules();
		for(CrySLRule r : rules) {
			exList.add(r.getClassName());
		}
		return exList;
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
		return includeList;
	}


	protected CG callGraphAlogrithm() {
		return CG.CHA;
	}

	protected String sootClassPath() {
		return "";
	}

	protected abstract String applicationClassPath();
	
	protected String softwareIdentifier(){
		return "";
	};
	
	protected String getOutputFolder(){
		return null;
	};
	

	protected boolean enableVisualization(){
		return false;
	};
	
	protected boolean sarifReport() {
		return false;
	}
	
	protected boolean providerDetection() {
		return true;
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
	
	private static void commandLineParserErrorMessage(ParseException e) {
		LOGGER.error("An error occured while trying to parse the command line arguments: ", e);
		LOGGER.error("\nThe default command for running CryptoAnalyis is: \n"
				+ "java -cp <jar_location_of_cryptoanalysis> crypto.HeadlessCryptoScanner \\\r\n" + 
				"      --rulesDir=<absolute_path_to_crysl_source_code_format_rules> \\\r\n" + 
				"      --applicationCp=<absolute_application_path>\n"
				+ "\nAdditional arguments that can be used are:\n"
				+ "--cg=<selection_of_call_graph_for_analysis (CHA, SPARK-LIBRARY, SPARK)>\n"
				+ "--rulesInSrc (specifies that rules are in source format)\n"
				+ "--sootCp=<absolute_path_of_whole_project>\n"
				+ "--softwareIdentifier=<identifier_for_labelling_output_files>\n"
				+ "--reportDir=<directory_location_for_cognicrypt_report>\n"
				+ "--csvReportFile=<summary_report_for_finding_csv_files>\n"
				+ "--preanalysis (enables pre-analysis)\n"
				+ "--visualization (enables the visualization, but also requires --reportDir option to be set)\n"
				+ "--sarifReport (enables sarif report)\n"
				+ "--providerDetection (enables provider detection analysis)\n");
	}
}
