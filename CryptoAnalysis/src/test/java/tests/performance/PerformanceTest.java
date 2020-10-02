package tests.performance;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.CrySLAnalysisListener;
import crypto.analysis.CrySLRulesetSelector;
import crypto.analysis.CrySLRulesetSelector.RuleFormat;
import crypto.analysis.CrySLRulesetSelector.Ruleset;
import crypto.exceptions.CryptoAnalysisException;
import crypto.rules.CrySLRule;
import soot.G;
import test.IDEALCrossingTestingFramework;
import tests.headless.MavenProject;


/**
 * 
 * @author kummitasriteja
 * Class that is used to record some performance metrics with respective to CryptoAnalysis on 
 * some set of benchmark projects and storing them in google sheet online.
 * This class requires the following parameters to run,
 * 		recent commit id
 * 		branch name
 * 		url of the repository
 * 		credentials of google sheet.
 * Ex. 
 * mvn -Dtest=PerformanceTest test -DcommitId=test-commit-id -DbranchName=testBranch -DgitUrl=https://helloworld.com 
 * 		-DgoogleSheetCredentials={\"test_key\": \"test_value\"}
 * 
 * This can be run in jenkins as part of the build by using the following command,
 * 		mvn -Dtest=PerformanceTest test -DcommitId=${GIT_COMMIT} -DbranchName=${GIT_BRANCH} -DgitUrl=${GIT_URL}
 * 			-DgoogleSheetCredentials=<credentials>
 */
@RunWith(Parameterized.class)
public class PerformanceTest{

	private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceTest.class);
	
	private static boolean VISUALIZATION = false;
	private static final String GIT_DOWNLOAD_PATH = "../CryptoAnalysisTargets/PerformanceBenchmarkProjects/";
	HeadlessCryptoScanner scanner;
	BenchmarkProject curProj;
	private static final String PARAM_COMMIT_ID = "commitId";
	private static final String PARAM_GIT_BRANCH_NAME = "branchName";
	private static final String PARAM_GIT_URL = "gitUrl";
	private static final String PARAM_GOOGLE_SHEET_CREDENTIALS = "googleSheetCredentials";
	private Map<String, String> observations = new HashMap<>();
	private Stopwatch analysisTime = Stopwatch.createUnstarted();

	@Before
	public void setup() throws IOException, GeneralSecurityException {
		String googleSheetCreds = System.getProperty(PARAM_GOOGLE_SHEET_CREDENTIALS);
		GoogleSpreadsheetWriter.createSheet(curProj.getName(), 
				curProj.getGitUrl(), 
				Arrays.asList(new String[] {
						SpreadSheetConstants.GIT_COMMIT_ID, 
						SpreadSheetConstants.ANALYSIS_TIME, 
						SpreadSheetConstants.MEMORY_USED, 
						SpreadSheetConstants.SOOT_REACHABLE_METHODS, 
						SpreadSheetConstants.NO_OF_RULES, 
						SpreadSheetConstants.NO_OF_SEEDS, 
						SpreadSheetConstants.NO_OF_SECURE_OBJECTS,
						SpreadSheetConstants.NO_OF_FINDINGS
						}),
				googleSheetCreds);
	}

	protected MavenProject createAndCompile(String mavenProjectPath) {
		MavenProject mi = new MavenProject(mavenProjectPath);
		mi.compile();
		return mi;
	}

	@SuppressWarnings("static-access")
	protected HeadlessCryptoScanner createScanner(MavenProject mp, BenchmarkProject proj, String commitId, String branchUrl, Ruleset... rulesets) {
		G.v().reset();
		HeadlessCryptoScanner scanner = new HeadlessCryptoScanner() {
			@Override
			protected String sootClassPath() {
				return mp.getBuildDirectory()
						+ (mp.getFullClassPath().equals("") ? "" : File.pathSeparator + mp.getFullClassPath());
			}

			@Override
			protected List<CrySLRule> getRules() {
				try {
					return CrySLRulesetSelector.makeFromRuleset(IDEALCrossingTestingFramework.RULES_BASE_DIR, RuleFormat.SOURCE, rulesets);
				} catch (CryptoAnalysisException e) {
					LOGGER.error("Error happened when getting the CrySL rules from the specified directory: "+IDEALCrossingTestingFramework.RULES_BASE_DIR, e);
				}
				return null;
			}

			@Override
			protected String applicationClassPath() {
				return mp.getBuildDirectory();
			}


			@Override
			public CrySLAnalysisListener getAdditionalListener() {
				return new PerformanceReportListener(getRules(), observations);
			}

			@Override
			protected String getOutputFolder() {
				File file = new File("cognicrypt-output/");
				file.mkdirs();
				return VISUALIZATION ? file.getAbsolutePath() : super.getOutputFolder();
			}

			@Override
			protected boolean enableVisualization() {
				return VISUALIZATION;
			}
		};
		return scanner;
	}

	@SuppressWarnings("static-access")
	protected HeadlessCryptoScanner createScanner(BenchmarkProject proj, String commitId, String branchUrl, Ruleset... rulesets) {
		G.v().reset();
		HeadlessCryptoScanner scanner = new HeadlessCryptoScanner() {
			@Override
			protected String sootClassPath() {
				return proj.getSootClassPath();
			}

			@Override
			protected List<CrySLRule> getRules() {
				try {
					return CrySLRulesetSelector.makeFromRuleset(IDEALCrossingTestingFramework.RULES_BASE_DIR, RuleFormat.SOURCE, rulesets);
				} catch (CryptoAnalysisException e) {
					LOGGER.error("Error happened when getting the CrySL rules from the specified directory: "+IDEALCrossingTestingFramework.RULES_BASE_DIR, e);
				}
				return null;
			}

			@Override
			protected String applicationClassPath() {
				return new File(proj.getProjectPath()).getAbsolutePath();
			}


			@Override
			protected CrySLAnalysisListener getAdditionalListener() {
				return new PerformanceReportListener(getRules(), observations);
			}

			@Override
			protected String getOutputFolder() {
				File file = new File("cognicrypt-output/");
				file.mkdirs();
				return VISUALIZATION ? file.getAbsolutePath() : super.getOutputFolder();
			}

			@Override
			protected boolean enableVisualization() {
				return VISUALIZATION;
			}
		};
		return scanner;
	}

	@Parameters
	public static Iterable<Object[]> data() {
		ArrayList<Object[]> params = Lists.newArrayList();
		BenchmarkProject project1 = new BenchmarkProject("Bitpay", 
				"../CryptoAnalysisTargets/PerformanceBenchmarkProjects/bitpay", 
				"https://github.com/bitpay/java-bitpay-client.git", 
				"32a8e9e08ef293e7d138584462c5c488ffe5f196",
				"", 
				true, 
				new Ruleset[] {Ruleset.JavaCryptographicArchitecture}
				);
		BenchmarkProject project2 = new BenchmarkProject("Aerogear-Crypto", 
				"../CryptoAnalysisTargets/PerformanceBenchmarkProjects/aerogear-crypto", 
				"https://github.com/aerogear/aerogear-crypto-java.git", 
				"7f73045d46a21260fbff5ac4a9d66aec764ec3c8",
				"", 
				true, 
				new Ruleset[] {Ruleset.JavaCryptographicArchitecture, Ruleset.BouncyCastle}
				);
		BenchmarkProject project3 = new BenchmarkProject("Commons-Crypto", 
				"../CryptoAnalysisTargets/PerformanceBenchmarkProjects/commons-crypto", 
				"https://github.com/apache/commons-crypto.git", 
				"02e6f9efccae6ee4d57336bfb5d08271737b4e29",
				"", 
				true, 
				new Ruleset[] {Ruleset.JavaCryptographicArchitecture}
				);
		params.add(new Object[] {project1});
		params.add(new Object[] {project2});
		params.add(new Object[] {project3});
		return params;
	}

	public PerformanceTest(BenchmarkProject proj) {
		this.curProj = proj;
	}
	
	private String getCommitUrl(String gitUrl, String gitCommitId) {
		String commitUrl = "";
		if (gitUrl != null && gitCommitId != null) {
	        String[] gitUrlList = gitUrl.split("\\.git");
	        commitUrl = gitUrlList[0] + File.separator + "commit" + File.separator + gitCommitId;
		}
		return commitUrl;
	}
	
	private List<Object> asCSVLine() {
		return Arrays.asList(new String[] { 
				observations.get(SpreadSheetConstants.HYPERLINK_COMMIT_ID),
				observations.get(SpreadSheetConstants.ANALYSIS_TIME),
				observations.get(SpreadSheetConstants.MEMORY_USED),
				observations.get(SpreadSheetConstants.SOOT_REACHABLE_METHODS),
				observations.get(SpreadSheetConstants.NO_OF_RULES),
				observations.get(SpreadSheetConstants.NO_OF_SEEDS),
				observations.get(SpreadSheetConstants.NO_OF_SECURE_OBJECTS),
				observations.get(SpreadSheetConstants.NO_OF_FINDINGS),
				});
	}
	
	private void createHyperLink(String gitUrl, String gitCommitId) {
		String hyperLinkForCommit = "=HYPERLINK(\"" + getCommitUrl(gitUrl, gitCommitId) + "\"; \"" + gitCommitId + "\")";
		observations.put(SpreadSheetConstants.HYPERLINK_COMMIT_ID, hyperLinkForCommit);
	}
	
	private void cloneRepository() {
		try {
			Git git = Git.cloneRepository().setURI(curProj.getGitUrl()).setDirectory(new File(curProj.getProjectPath())).call();
			git.checkout().setName(curProj.getCommitId()).call();
		} catch (InvalidRemoteException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unused")
	@Test
	public void test() throws Exception {
		cloneRepository();
		
		String gitCommitId = String.valueOf(System.currentTimeMillis()), branchName = "", gitUrl = "", googleSheetCreds = "";
		gitCommitId = System.getProperty(PARAM_COMMIT_ID);		
		if (System.getProperty(PARAM_GIT_BRANCH_NAME) != null)
			branchName = System.getProperty(PARAM_GIT_BRANCH_NAME);
		gitUrl = System.getProperty(PARAM_GIT_URL);
		googleSheetCreds = System.getProperty(PARAM_GOOGLE_SHEET_CREDENTIALS);
		createHyperLink(gitUrl, gitCommitId);
		
		analysisTime.start();
		if (curProj.getIsMavenProject()) {
			MavenProject mavenProject = createAndCompile(new File(curProj.getProjectPath()).getAbsolutePath());
			scanner = createScanner(mavenProject, curProj, gitCommitId, getCommitUrl(gitUrl, gitCommitId), curProj.getRuleSet());
		} else {
			scanner = createScanner(curProj, gitCommitId, getCommitUrl(gitUrl, gitCommitId), curProj.getRuleSet());
		}
		scanner.exec();
		long elapsed = analysisTime.elapsed(TimeUnit.SECONDS);
		analysisTime.stop();
		observations.put(SpreadSheetConstants.ANALYSIS_TIME, String.valueOf(elapsed));
		
		GoogleSpreadsheetWriter.write(asCSVLine(), curProj.getName(), googleSheetCreds);
		FileUtils.cleanDirectory(new File(curProj.getProjectPath() + File.separator + ".." + File.separator)); 
	}
}
