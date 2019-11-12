package de.fraunhofer.iem.crypto;

import boomerang.callgraph.BoomerangICFG;
import boomerang.callgraph.ObservableICFG;
import boomerang.callgraph.ObservableStaticICFG;
import boomerang.preanalysis.BoomerangPretransformer;
import com.google.common.collect.Lists;
import crypto.analysis.CrySLResultsReporter;
import crypto.analysis.CryptoScanner;
import crypto.analysis.errors.AbstractError;
import crypto.cryptslhandler.CrySLModelReader;
import crypto.reporting.CollectErrorListener;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLRuleReader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.config.SootConfigForAndroid;
import soot.options.Options;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CogniCryptAndroidAnalysis {

	private static final Logger logger = LoggerFactory.getLogger(CogniCryptAndroidAnalysis.class);
	private File apkFile;
	private File platformsDirectory;
	private File rulesLocation;
	private final Collection<String> applicationClassFilter;

	/**
	 * @return The path to the .apk file that shall be analyzed.
	 */
	public File getApkFile(){
		return apkFile;
	}

	private void setApkFile(File apkFile){
		if (!apkFile.exists() || !apkFile.isFile())
			throw new IllegalArgumentException("Argument 'apkFile' does not point to a file.");
		this.apkFile = apkFile;
	}

	/**
	 * @return The location of the Android platform which the .apk targets.
	 */
	public File getPlatformsDirectory(){
		return platformsDirectory;
	}

	private void setPlatformsDirectory(File platformsDirectory){
		if (!platformsDirectory.exists() || !platformsDirectory.isDirectory())
			throw new IllegalArgumentException("Argument 'platformsDirectory' is not a dictionary.");
		this.platformsDirectory = platformsDirectory;
	}

	/**
	 * @return The location of the CrySL rules.
	 */
	public File getRulesLocation(){
		return rulesLocation;
	}

	private void setRulesLocation(File rulesLocation){
		if (!rulesLocation.exists())
			throw new IllegalArgumentException("Argument 'rulesLocation' is not valid.");
		this.rulesLocation = rulesLocation;
	}

	/**
	 * @return All full qualified class names that will be analyzed. If empty all classes of the Android App will be analyzed.
	 */
	public Collection<String> getApplicationClassFilter(){
		return applicationClassFilter;
	}

    /**
     * @param apkFile The Android platforms directory.
     * @param platformsDirectory The Android platform which the .apk targets.
     * @param rulesLocation The CrySL rules directory.
     */
    public CogniCryptAndroidAnalysis(File apkFile, File platformsDirectory, File rulesLocation) {
        this(apkFile, platformsDirectory, rulesLocation, Collections.EMPTY_LIST);
    }

    /**
     * @param apkFile The Android platforms directory.
     * @param platformsDirectory The Android platform which the .apk targets.
     * @param rulesLocation The CrySL rules directory.
     * @param applicationClassFilter Collection of full qualified class names the analysis shall analyze explicitly.
     *                               Settings this can increase analysis runtime. If null or empty this parameter gets ignored.
     */
    public CogniCryptAndroidAnalysis(File apkFile, File platformsDirectory, File rulesLocation,
                                     Collection<String> applicationClassFilter) {
        if (apkFile == null)
            throw new IllegalArgumentException("Argument 'apkFile' must not be null.");
        if (platformsDirectory == null)
            throw new IllegalArgumentException("Argument 'platformsDirectory' must not be null.");
        if (rulesLocation == null)
            throw new IllegalArgumentException("Argument 'rulesLocation' must not be null.");

        setApkFile(apkFile);
        setPlatformsDirectory(platformsDirectory);
        setRulesLocation(rulesLocation);

        if (applicationClassFilter == null)
            this.applicationClassFilter = Collections.EMPTY_LIST;
        else
            this.applicationClassFilter = applicationClassFilter;
    }

	/**
	 * @param apkFile Absolute path of the Android platforms directory.
	 * @param platformsDirectory Absolute path of the Android platform which the .apk targets.
	 * @param rulesLocation Absolute path of the CrySL rules directory.
	 */
	public CogniCryptAndroidAnalysis(String apkFile, String platformsDirectory, String rulesLocation) {
		this(apkFile, platformsDirectory, rulesLocation, Collections.EMPTY_LIST);
	}

	/**
	 * @param apkFile Absolute path of the Android platform which the .apk targets.
	 * @param platformsDirectory Absolute path of the Android platform which the .apk targets.
	 * @param rulesLocation Absolute path of the CrySL rules directory.
	 * @param applicationClassFilter Collection of full qualified class names the analysis shall analyze explicitly.
	 *                               Settings this can increase analysis runtime. If null or empty this parameter gets ignored.
	 */
	public CogniCryptAndroidAnalysis(String apkFile, String platformsDirectory, String rulesLocation,
			Collection<String> applicationClassFilter) {
		if (StringUtils.isBlank(apkFile))
			throw new IllegalArgumentException("Argument 'apkFile' must not be null or empty.");
		if (StringUtils.isBlank(platformsDirectory))
			throw new IllegalArgumentException("Argument 'platformsDirectory' must not be null or empty.");
		if (StringUtils.isBlank(rulesLocation))
			throw new IllegalArgumentException("Argument 'rulesLocation' must not be null or empty.");

		setApkFile(new File(apkFile));
		setPlatformsDirectory(new File(platformsDirectory));
		setRulesLocation(new File(rulesLocation));

		if (applicationClassFilter == null)
			this.applicationClassFilter = Collections.EMPTY_LIST;
		else
			this.applicationClassFilter = applicationClassFilter;
	}

	public static void main(String... args) {
		CogniCryptAndroidAnalysis analysis = new CogniCryptAndroidAnalysis(args[0], args[1], args[2], Lists.newArrayList());
		analysis.run();
	}

	public Collection<AbstractError> run() {
		logger.info("Running static analysis on APK file " + apkFile);
		logger.info("with Android Platforms dir " + platformsDirectory);
		constructCallGraph();
		return runCryptoAnalysis();
	}

	private void constructCallGraph() {
		InfoflowAndroidConfiguration config = new InfoflowAndroidConfiguration();
		config.setCallgraphAlgorithm(InfoflowConfiguration.CallgraphAlgorithm.CHA);
		config.getCallbackConfig().setEnableCallbacks(false);
		config.setCodeEliminationMode(InfoflowConfiguration.CodeEliminationMode.NoCodeElimination);
		config.getAnalysisFileConfig().setAndroidPlatformDir(platformsDirectory.getAbsolutePath());
		config.getAnalysisFileConfig().setTargetAPKFile(apkFile.getAbsolutePath());
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

		final CrySLResultsReporter reporter = new CrySLResultsReporter();
		CollectErrorListener errorListener = new CollectErrorListener();
		reporter.addReportListener(errorListener);
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
		List<CryptSLRule> rules = getRules();
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

	protected List<CryptSLRule> getRules() {
		List<CryptSLRule> rules = Lists.newArrayList();
		if (rulesLocation == null) {
			
			// TODO: Create a custom CryptoAnalysisException (or CogniCryptException)
			// TODO: Update when constant changed CryptoAnalysis
			throw new RuntimeException(
					"Please specify a directory the CrySL rules (" + CrySLModelReader.cryslFileEnding + " Files) are located in.");
		}
		
		// TODO: Why iterate here through all files if CryptoAnalysis could do this
		// Suggestion: 
		// List<CryptSLRule> rules = CryptSLRuleReader.readFromSourceFolder(file, recursive: true)
		// if (rules.isEmpty() {...}
		// return rules;
		
		File[] listFiles = rulesLocation.listFiles();
		for (File file : listFiles) {
			
			// TODO: Update when constant changed CryptoAnalysis
			if (file != null && file.getName().endsWith(CrySLModelReader.cryslFileEnding)) {
				rules.add(CryptSLRuleReader.readFromSourceFile(file));
			}
		}
		if (rules.isEmpty())
			System.out.println("CogniCrypt did not find any rules to start the analysis for. \n "
					+ "It checked for rules in " + rulesLocation);
		return rules;
	}

}
