package bench;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import boomerang.cfg.ExtendedICFG;
import boomerang.cfg.IExtendedICFG;
import boomerang.preanalysis.PreparationTransformer;
import crypto.analysis.CogniCryptCLIReporter;
import crypto.analysis.CryptSLAnalysisListener;
import crypto.analysis.CryptoScanner;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLRuleReader;
import crypto.rules.StateNode;
import ideal.debug.IDebugger;
import ideal.debug.NullDebugger;
import soot.G;
import soot.MethodOrMethodContext;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Transformer;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.options.Options;
import soot.util.queue.QueueReader;
import typestate.TypestateDomainValue;

public class DerbyScanner {
	public static String RESOURCE_PATH = "D:\\CROSSING\\CryptoAnalysis\\CryptoAnalysis\\build\\rules";
	private static String BENCHMARK_FOLDER = "D:\\CROSSING\\benchmark\\Derby\\db-derby-10.13.1.1-bin\\lib";
	private static CogniCryptCLIReporter reporter;
	private static ExtendedICFG icfg;

	public static void main(String... args) {
		initializeSootWithEntryPoint(getJarFiles(BENCHMARK_FOLDER));
		analyse();
	}
	private static File[] getJarFiles( String dirName){
        File dir = new File(dirName);
        return dir.listFiles(new FilenameFilter() { 
                 public boolean accept(File dir, String filename)
                      { return filename.endsWith(".jar"); }
        } );
    }
	
	private static void analyse() {
		PackManager.v().getPack("wjtp").add(new Transform("wjtp.prepare", new PreparationTransformer()));
		Transform transform = new Transform("wjtp.ifds", createAnalysisTransformer());
		PackManager.v().getPack("wjtp").add(transform);
		PackManager.v().runPacks();
	}

	private static Transformer createAnalysisTransformer() {
		return new SceneTransformer() {
			
			@Override
			protected void internalTransform(String phaseName, Map<String, String> options) {
				icfg = new ExtendedICFG(new JimpleBasedInterproceduralCFG(false));
				reporter = new CogniCryptCLIReporter(icfg, null);
				System.out.println("Soot Classes: "+ Scene.v().getClasses().size());
				System.out.println("Reachable Methods: "+ Scene.v().getReachableMethods().size());
				CryptoScanner scanner = new CryptoScanner(getRules()) {

					@Override
					public IExtendedICFG icfg() {
						return icfg;
					}

					@Override
					public CryptSLAnalysisListener analysisListener() {
						return reporter;
					}

					@Override
					public IDebugger<TypestateDomainValue<StateNode>> debugger() {
						return new NullDebugger<>();
					}

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
				rules.add(CryptSLRuleReader.readFromFile(file));
			}
		}
		return rules;
	}

	private static void initializeSootWithEntryPoint(File[] files) {
		G.v().reset();
		Options.v().set_whole_program(true);
//		Options.v().setPhaseOption("cg", "on");
		Options.v().set_output_format(Options.output_format_none);
//		Options.v().set_no_bodies_for_excluded(true);
		Options.v().set_allow_phantom_refs(true);
//		 Options.v().setPhaseOption("cg", "all-reachable:true");
		 Options.v().setPhaseOption("cg.spark", "on,simulate-natives:false");
//		 Options.v().setPhaseOption("cg", "implicit-entry:false");
//		Options.v().set_prepend_classpath(true);
		ArrayList<String> jarFiles = Lists.newArrayList();
		for(File f : files){
			jarFiles.add(f.getAbsolutePath());
		}
		Options.v().set_exclude(excludedPackages());
		Options.v().set_process_dir(jarFiles);
		Scene.v().loadNecessaryClasses();
	}
	private static List<String> excludedPackages() {
		List<String> excludedPackages = new LinkedList<>();
		excludedPackages.add("sun.*");
		excludedPackages.add("javax.*");
		excludedPackages.add("java.*");
		excludedPackages.add("com.sun.*");
		excludedPackages.add("com.ibm.*");
		excludedPackages.add("org.xml.*");
		excludedPackages.add("org.w3c.*");
		excludedPackages.add("apple.awt.*");
		excludedPackages.add("com.apple.*");
		return excludedPackages;
	}
}
