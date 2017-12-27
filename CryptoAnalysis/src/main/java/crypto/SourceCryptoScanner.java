package crypto;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import crypto.analysis.CogniCryptCLIReporter;
import crypto.analysis.CrySLAnalysisResultsAggregator;
import crypto.analysis.CryptoScanner;
import crypto.analysis.ICrySLResultsListener;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLRuleReader;
import soot.G;
import soot.PackManager;
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

public class SourceCryptoScanner {
	public static String RESOURCE_PATH = "rules";
	private static CrySLAnalysisResultsAggregator reporter;
	private static JimpleBasedInterproceduralCFG icfg;
	private static CG callGraphAlogrithm = CG.SPARK;
	
	private static enum CG {
		CHA, SPARK_LIBRARY, SPARK
	}
	
	public static void main(String... args) {
		if(args.length > 2)
			RESOURCE_PATH = args[2];
		if(args.length > 3){
			if(args[3].equalsIgnoreCase("cha")){
				callGraphAlogrithm = CG.CHA;
			}
			if(args[3].equalsIgnoreCase("spark")){
				callGraphAlogrithm = CG.SPARK;
			} 
			if(args[3].equalsIgnoreCase("spark-library")){
				callGraphAlogrithm = CG.SPARK_LIBRARY;
			} 
			if(args[3].equalsIgnoreCase("library")){
				callGraphAlogrithm = CG.SPARK_LIBRARY;
			} 
		}
		initializeSootWithEntryPointAllReachable(args[0], args[1]);
		System.out.println("Using call graph algorithm " + callGraphAlogrithm);
		analyse();
		
	}

	public static void runAnalysis(String cp, String mainClass, String resPath) {
		initializeSootWithEntryPoint(cp, mainClass);
		RESOURCE_PATH = resPath;
		analyse();
	}
	public static void runAnalysisAllReachable(String applicationClasses, String cp, String resPath) {
		initializeSootWithEntryPointAllReachable(applicationClasses,cp);
		RESOURCE_PATH = resPath;
		analyse();
	}
	private static void analyse() {
//		PackManager.v().getPack("wjtp").add(new Transform("wjtp.prepare", new PreparationTransformer()));
		Transform transform = new Transform("wjtp.ifds", createAnalysisTransformer());
		PackManager.v().getPack("wjtp").add(transform);
		PackManager.v().getPack("cg").apply();
		PackManager.v().getPack("wjtp").apply();
	}

	private static Transformer createAnalysisTransformer() {
		return new SceneTransformer() {
			
			@Override
			protected void internalTransform(String phaseName, Map<String, String> options) {
				icfg = new JimpleBasedInterproceduralCFG(false);
				reporter = new CrySLAnalysisResultsAggregator(null);
				reporter.addReportListener(new CogniCryptCLIReporter());
				CryptoScanner scanner = new CryptoScanner(getRules()) {

					@Override
					public BiDiInterproceduralCFG<Unit, SootMethod> icfg() {
						return icfg;
					}

					@Override
					public CrySLAnalysisResultsAggregator getAnalysisListener() {
						return reporter;
					}

//					@Override
//					public IDebugger<TypestateDomainValue<StateNode>> debugger() {
//						return new NullDebugger<>();
//					}

					@Override
					public boolean isCommandLineMode() {
						return true;
					}

				};
				scanner.scan();
				System.out.println(reporter);
			}
		};
	}

	protected static List<CryptSLRule> getRules() {
		List<CryptSLRule> rules = Lists.newArrayList();

		File[] listFiles = new File(RESOURCE_PATH).listFiles();
		for (File file : listFiles) {
			if (file.getName().endsWith(".cryptslbin")) {
				System.out.println(file.getName());
				rules.add(CryptSLRuleReader.readFromFile(file));
			}
		}
		return rules;
	}

	private static void initializeSootWithEntryPoint(String sootClassPath, String mainClass) {
		G.v().reset();
		Options.v().set_whole_program(true);
		Options.v().setPhaseOption("cg.spark", "on");
		Options.v().set_output_format(Options.output_format_none);
		Options.v().set_no_bodies_for_excluded(true);
		Options.v().set_allow_phantom_refs(true);
		// Options.v().setPhaseOption("cg", "all-reachable:true");

		Options.v().set_prepend_classpath(true);
		Options.v().set_soot_classpath(sootClassPath);
		Options.v().set_main_class(mainClass);

		Scene.v().addBasicClass(mainClass, SootClass.BODIES);
		Scene.v().loadNecessaryClasses();
		SootClass c = Scene.v().forceResolve(mainClass, SootClass.BODIES);
		if (c != null) {
			c.setApplicationClass();
		}
		SootMethod methodByName = c.getMethodByName("main");
		List<SootMethod> ePoints = new LinkedList<>();
		ePoints.add(methodByName);
		Scene.v().setEntryPoints(ePoints);
	}
	private static void initializeSootWithEntryPointAllReachable(String applicationClasses, String sootClassPath) {
		G.v().reset();
		Options.v().set_whole_program(true);
		switch(callGraphAlogrithm){
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
//		Options.v().setPhaseOption("cg", "all-reachable:true,apponly:true");

		Options.v().set_prepend_classpath(true);
		Options.v().set_soot_classpath(sootClassPath);
		Options.v().set_process_dir(Lists.newArrayList(applicationClasses));
		Scene.v().loadNecessaryClasses();
	}
}
