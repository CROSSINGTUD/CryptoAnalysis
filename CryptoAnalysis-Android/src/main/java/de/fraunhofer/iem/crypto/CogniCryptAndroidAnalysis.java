package de.fraunhofer.iem.crypto;

import java.io.File;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Lists;
import boomerang.callgraph.BoomerangICFG;
import boomerang.callgraph.ObservableICFG;
import boomerang.callgraph.ObservableStaticICFG;
import boomerang.preanalysis.BoomerangPretransformer;
import crypto.analysis.CrySLResultsReporter;
import crypto.analysis.CryptoScanner;
import crypto.analysis.errors.AbstractError;
import crypto.exceptions.CryptoAnalysisException;
import crypto.reporting.CollectErrorListener;
import crypto.rules.CrySLRule;
import crypto.rules.CrySLRuleReader;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.config.SootConfigForAndroid;
import soot.options.Options;
import crypto.cryslhandler.CrySLModelReader;
import crypto.reporting.CommandLineReporter;

public class CogniCryptAndroidAnalysis {
	public static void main(String... args) {
		CogniCryptAndroidAnalysis analysis;
		if (args[3] != null) {
			analysis = new CogniCryptAndroidAnalysis(args[0], args[1], args[2], args[3], Lists.<String>newArrayList());
		} else {
			analysis = new CogniCryptAndroidAnalysis(args[0], args[1], args[2], Lists.<String>newArrayList());
		}
		analysis.run();
	}

	private static final Logger logger = LoggerFactory.getLogger(CogniCryptAndroidAnalysis.class);
	private final String apkFile;
	private final String platformsDirectory;
	private final String rulesDirectory;
	private final String outputDir;
	private final Collection<String> applicationClassFilter;

	public CogniCryptAndroidAnalysis(String apkFile, String platformsDirectory, String rulesDirectory,
			Collection<String> applicationClassFilter) {
		this(apkFile, platformsDirectory, rulesDirectory, null, applicationClassFilter);
	}

	public CogniCryptAndroidAnalysis(String apkFile, String platformsDirectory, String rulesDirectory, String outputDir,
			Collection<String> applicationClassFilter) {
		this.apkFile = apkFile;
		this.platformsDirectory = platformsDirectory;
		this.rulesDirectory = rulesDirectory;
		this.applicationClassFilter = applicationClassFilter;
		this.outputDir = outputDir;
	}

	public Collection<AbstractError> run() {
		logger.info("Running static analysis on APK file " + apkFile);
		logger.info("with Android Platforms dir " + platformsDirectory);
		constructCallGraph();
		return runCryptoAnalysis();
	}
	
	public String getApkFile(){
		return apkFile;
	}
	
	public String getPlatformsDirectory(){
		return platformsDirectory;
	}
	
	public String getRulesDirectory(){
		return rulesDirectory;
	}
	
	public Collection<String> getApplicationClassFilter(){
		return applicationClassFilter;
	}

	private void constructCallGraph() {
		InfoflowAndroidConfiguration config = new InfoflowAndroidConfiguration();
		config.setCallgraphAlgorithm(InfoflowConfiguration.CallgraphAlgorithm.CHA);
		config.getCallbackConfig().setEnableCallbacks(false);
		config.setCodeEliminationMode(InfoflowConfiguration.CodeEliminationMode.NoCodeElimination);
		config.getAnalysisFileConfig().setAndroidPlatformDir(platformsDirectory);
		config.getAnalysisFileConfig().setTargetAPKFile(apkFile);
		config.setMergeDexFiles(true);
		SetupApplication flowDroid = new SetupApplication(config);
		SootConfigForAndroid sootConfigForAndroid = new SootConfigForAndroid() {
			@Override
			public void setSootOptions(Options options, InfoflowConfiguration config) {
				options.set_keep_line_number(true);
			}
		};
		flowDroid.setSootConfig(sootConfigForAndroid);
		logger.info("Constructing call graph");
		flowDroid.constructCallgraph();
		logger.info("Done constructing call graph");
	}

	private Collection<AbstractError> runCryptoAnalysis() {
		prepareAnalysis();

		final ObservableStaticICFG icfg = new ObservableStaticICFG(new BoomerangICFG(false));
		List<CrySLRule> rules = getRules();
		
		final CrySLResultsReporter reporter = new CrySLResultsReporter();
		CollectErrorListener errorListener = new CollectErrorListener();
		reporter.addReportListener(errorListener);
		reporter.addReportListener(new CommandLineReporter(outputDir, rules));
		CryptoScanner scanner = new CryptoScanner() {

			@Override
			public ObservableICFG<Unit, SootMethod> icfg() {
				return icfg;
			}

			@Override
			public CrySLResultsReporter getAnalysisListener() {
				return reporter;
			}

		};
		
		logger.info("Loaded " + rules.size() + " CrySL rules");
		logger.info("Running CogniCrypt Analysis");
		scanner.scan(rules);
		logger.info("Terminated CogniCrypt Analysis");
		System.gc();
		return errorListener.getErrors();
	}

	private void prepareAnalysis() {
        BoomerangPretransformer.v().reset();
        BoomerangPretransformer.v().apply();

        //Setting application classes to be the set of classes where we have found .java files for. Hereby we ignore library classes and reduce the analysis time.
        if(!applicationClassFilter.isEmpty()) {
	        for(SootClass c : Scene.v().getClasses()){
	            for(String filter : applicationClassFilter) {
	            	if(c.getName().contains(filter)) {
	            		c.setApplicationClass();
	            	} else {
	            		c.setLibraryClass();
	            	}
	            }
	        }
        }
        logger.info("Application classes: "+ Scene.v().getApplicationClasses().size());
        logger.info("Library classes: "+ Scene.v().getLibraryClasses().size());
    }

	protected List<CrySLRule> getRules() {
		List<CrySLRule> rules = Lists.newArrayList();
		if (rulesDirectory == null) {
			throw new RuntimeException(
					"Please specify a directory the CrySL rules ( " + CrySLModelReader.cryslFileEnding +" Files) are located in.");
		}
		File[] listFiles = new File(rulesDirectory).listFiles();
		for (File file : listFiles) {
			if (file != null && file.getName().endsWith(CrySLModelReader.cryslFileEnding)) {
				try {
					rules.add(CrySLRuleReader.readFromSourceFile(file));
				} catch (CryptoAnalysisException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		if (rules.isEmpty())
			System.out.println("CogniCrypt did not find any rules to start the analysis for.\n"
								+ "It checked for rules in "+rulesDirectory);
		return rules;
	}

}
