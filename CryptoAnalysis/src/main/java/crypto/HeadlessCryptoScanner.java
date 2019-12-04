package crypto;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;

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
import crypto.preanalysis.SeedFactory;
import crypto.providerdetection.ProviderDetection;
import crypto.reporting.CSVReporter;
import crypto.reporting.CommandLineReporter;
import crypto.reporting.ErrorMarkerListener;
import crypto.reporting.SARIFReporter;
import de.darmstadt.tu.crossing.crysl.rules.CrySLRule;
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
	private static List<CrySLRule> rules;
	private static final Logger LOGGER = LoggerFactory.getLogger(HeadlessCryptoScanner.class);

	public static enum CG {
		CHA, SPARK_LIBRARY, SPARK
	}

	public static void main(String... args) throws ParseException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		HeadlessCryptoScanner scanner = createFromOptions(args);
		scanner.exec();
	}

	public static HeadlessCryptoScanner createFromOptions(String... args) throws ParseException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		CommandLineParser parser = new DefaultParser();
		options = parser.parse(new HeadlessCryptoScannerOptions(), args);

		if (options.hasOption("rulesDir")) {
			String resourcesPath = options.getOptionValue("rulesDir");
			rules = CrySLRulesetSelector.makeFromPath(new File(resourcesPath), RuleFormat.SOURCE);
			
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
			initializeSootWithEntryPointAllReachable(false);
			LOGGER.info("Pre-Analysis soot setup done in {} ",stopwatch);
			checkIfUsesObject();
			LOGGER.info("Pre-Analysis  finished in {}", stopwatch);
		}
		if (!PRE_ANALYSIS || hasSeeds()) {
			LOGGER.info("Using call graph algorithm {}", callGraphAlogrithm());
			initializeSootWithEntryPointAllReachable(true);
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
								throw new RuntimeException("The visualization requires the option --reportDir");
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
					rules = providerDetection.doAnalysis(observableDynamicICFG, rules);
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
		}
		
		return rules = CrySLRulesetSelector.makeFromRuleset("src/main/resources/JavaCryptographicArchitecture", RuleFormat.SOURCE, Ruleset.JavaCryptographicArchitecture);
	}

	private void initializeSootWithEntryPointAllReachable(boolean wholeProgram) {
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
			throw new RuntimeException("No call graph option selected!");
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
			exList.add(Utils.getFullyQualifiedName(r));
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
		return false;
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
