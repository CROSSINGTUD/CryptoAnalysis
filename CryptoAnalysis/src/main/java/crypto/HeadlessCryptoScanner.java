package crypto;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;

import crypto.analysis.CrySLResultsReporter;
import crypto.analysis.CryptoScanner;
import crypto.preanalysis.SeedFactory;
import crypto.reporting.CSVReporter;
import crypto.reporting.CommandLineReporter;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLRuleReader;
import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.PackManager;
import soot.PhaseOptions;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Transformer;
import soot.Unit;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.options.Options;

public abstract class HeadlessCryptoScanner {
	private boolean hasSeeds;
	private static Stopwatch callGraphWatch;
	private static CommandLine options;

	public static enum CG {
		CHA, SPARK_LIBRARY, SPARK
	}

	public static void main(String... args) throws ParseException {
		CommandLineParser parser = new DefaultParser();
		options = parser.parse(new HeadlessOptions(), args);
		final String resourcesPath;
		if (options.hasOption("rulesDir"))
			resourcesPath = options.getOptionValue("rulesDir");
		else 
			resourcesPath = "rules";
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
				return options.getOptionValue("sootCp");
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
			protected String getRulesDirectory() {
				return resourcesPath;
			}
			
			@Override
			protected String getOutputFile(){
				return options.getOptionValue("reportFile");
			}

			@Override
			protected String getCSVOutputFile(){
				return options.getOptionValue("csvReportFile");
			}
		};
		sourceCryptoScanner.exec();
	}


	protected abstract String getCSVOutputFile();


	public void exec() {
		initializeSootWithEntryPointAllReachable(false);
		checkIfUsesObject();
		if (hasSeeds()) {
			System.out.println("Using call graph algorithm " + callGraphAlogrithm());
			initializeSootWithEntryPointAllReachable(true);
			analyse();
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
		s += "\tRules Directory: "+ getRulesDirectory() +"\n";
		s += "\tSootClassPath: "+ sootClassPath() +"\n\n";
		return s;
	}

	private Transformer createAnalysisTransformer() {
		return new SceneTransformer() {

			@Override
			protected void internalTransform(String phaseName, Map<String, String> options) {
				final JimpleBasedInterproceduralCFG icfg = new JimpleBasedInterproceduralCFG(false);
				List<CryptSLRule> rules = HeadlessCryptoScanner.this.getRules();
				CommandLineReporter fileReporter = new CommandLineReporter(getOutputFile(), rules);

				final CrySLResultsReporter reporter = new CrySLResultsReporter();
				CryptoScanner scanner = new CryptoScanner(rules) {

					@Override
					public BiDiInterproceduralCFG<Unit, SootMethod> icfg() {
						return icfg;
					}

					@Override
					public CrySLResultsReporter getAnalysisListener() {
						return reporter;
					}

					// @Override
					// public IDebugger<TypestateDomainValue<StateNode>>
					// debugger() {
					// return new NullDebugger<>();
					// }

					@Override
					public boolean isCommandLineMode() {
						return true;
					}

				};
				reporter.addReportListener(fileReporter);
				String csvOutputFile = getCSVOutputFile();
				if(csvOutputFile != null){
					reporter.addReportListener(new CSVReporter(csvOutputFile,softwareIdentifier(),rules,callGraphWatch.elapsed(TimeUnit.MILLISECONDS)));
				}
				scanner.scan();
			}
		};
	}

	protected List<CryptSLRule> getRules() {
		List<CryptSLRule> rules = Lists.newArrayList();
		if(getRulesDirectory() == null){
			throw new RuntimeException("Please specify a directory the CrySL rules (.cryptslbin Files) are located in.");
		}
		File[] listFiles = new File(getRulesDirectory()).listFiles();
		for (File file : listFiles) {
			if (file != null && file.getName().endsWith(".cryptslbin")) {
				rules.add(CryptSLRuleReader.readFromFile(file));
			}
		}
		if (rules.isEmpty())
			System.out.println(
					"CogniCrypt did not find any rules to start the analysis for. \n It checked for rules in "
							+ getRulesDirectory());
		return rules;
	}

	protected abstract String getRulesDirectory();
	
	private void initializeSootWithEntryPointAllReachable(boolean wholeProgram) {
		G.v().reset();

		Options.v().set_whole_program(wholeProgram);

		switch (callGraphAlogrithm()) {
		case CHA:
			Options.v().setPhaseOption("cg.cha", "on");
			Options.v().setPhaseOption("cg", "all-reachable:true");
			break;
		case SPARK_LIBRARY:
			Options.v().setPhaseOption("cg.spark", "on");
			Options.v().setPhaseOption("cg", "all-reachable:true,library:any-subtype");
			break;
		case SPARK:
			Options.v().setPhaseOption("cg.spark", "on");
			Options.v().setPhaseOption("cg", "all-reachable:true");
			break;
		default:
			throw new RuntimeException("No call graph option selected!");
		}
		Options.v().set_output_format(Options.output_format_none);
		Options.v().set_no_bodies_for_excluded(true);
		Options.v().set_allow_phantom_refs(true);

		Options.v().set_prepend_classpath(true);
		System.out.println((sootClassPath() + File.pathSeparator + pathToJCE()));
		Options.v().set_soot_classpath(sootClassPath() + File.pathSeparator + pathToJCE());
		Options.v().set_process_dir(Lists.newArrayList(applicationClassPath()));

		List<String> includeList = new LinkedList<String>();
		includeList.add("java.lang.*");
		Options.v().set_include(includeList);
		Options.v().set_full_resolver(true);
		Scene.v().loadNecessaryClasses();
		
		System.out.println("Finished initializing soot");

	}

	protected CG callGraphAlogrithm() {
		return CG.CHA;
	}

	protected abstract String sootClassPath();

	protected abstract String applicationClassPath();

	protected abstract String softwareIdentifier();
	
	protected abstract String getOutputFile();

	
	private static String pathToJCE() {
		// When whole program mode is disabled, the classpath misses jce.jar
		return System.getProperty("java.home") + File.separator + "lib" + File.separator + "jce.jar";
	}

}
