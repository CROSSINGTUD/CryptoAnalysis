package tests.providerdetection;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import boomerang.callgraph.ObservableDynamicICFG;
import boomerang.preanalysis.BoomerangPretransformer;
import crypto.analysis.CrySLRulesetSelector.Ruleset;
import crypto.providerdetection.ProviderDetection;
import soot.G;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.Transform;
import soot.Transformer;
import soot.options.Options;

public class ProviderDetectionTestingFramework extends ProviderDetection {
	
	private static final Ruleset defaultRuleset = Ruleset.JavaCryptographicArchitecture;
	private static final String rootRulesDirectory = System.getProperty("user.dir")+File.separator+"src"+File.separator+"main"+File.separator+"resources";
	private static final String defaultRulesDirectory = rootRulesDirectory+File.separator+defaultRuleset;
	private static final String sootClassPath = System.getProperty("user.dir") + File.separator+"target"+File.separator+"test-classes";
	
	/**
	 * This method is used to get the Soot classpath from `src/test/java`
	 * in order to run the JUnit test cases for Provider Detection
	 */
	public String getSootClassPath(){
		//Assume target folder to be directly in user directory
		File classPathDir = new File(sootClassPath);
		if (!classPathDir.exists()){
			throw new RuntimeException("Classpath for the test cases could not be found.");
		}
		return sootClassPath;
	}
	
	
	/**
	 * This method is used to setup Soot
	 */
	public void setupSoot(String sootClassPath, String mainClass) {
		G.v().reset();
		Options.v().set_whole_program(true);
		Options.v().setPhaseOption("cg.cha", "on");
//		Options.v().setPhaseOption("cg", "all-reachable:true");
		Options.v().set_output_format(Options.output_format_none);
		Options.v().set_no_bodies_for_excluded(true);
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_keep_line_number(true);
		Options.v().set_prepend_classpath(true);
		Options.v().set_soot_classpath(sootClassPath);
		SootClass c = Scene.v().forceResolve(mainClass, SootClass.BODIES);
		if (c != null) {
			c.setApplicationClass();
		}

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

		Options.v().set_include(includeList);
		Options.v().set_full_resolver(true);
		Scene.v().loadNecessaryClasses();
	}
	
	
	public void analyze() {
		Transform transform = new Transform("wjtp.ifds", createAnalysisTransformer());
		PackManager.v().getPack("wjtp").add(transform);
		PackManager.v().getPack("cg").apply();
		PackManager.v().getPack("wjtp").apply();
	}
	
	
	private Transformer createAnalysisTransformer() {
		return new SceneTransformer() {
			
			@Override
			protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
				BoomerangPretransformer.v().reset();
				BoomerangPretransformer.v().apply();
				ObservableDynamicICFG observableDynamicICFG = new ObservableDynamicICFG(false);
				setRulesDirectory(defaultRulesDirectory);
				doAnalysis(observableDynamicICFG, rootRulesDirectory);
			}
		};
	}
	
}
