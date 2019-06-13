package tests.performance;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Lists;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.CrySLAnalysisListener;
import crypto.analysis.CrySLRulesetSelector;
import crypto.analysis.CrySLRulesetSelector.Ruleset;
import crypto.rules.CryptSLRule;
import soot.G;
import test.IDEALCrossingTestingFramework;
import tests.headless.MavenProject;


@RunWith(Parameterized.class)
public class PerformanceTest{

	private static boolean VISUALIZATION = false;
	HeadlessCryptoScanner scanner;
	BenchmarkProject curProj;
	private static final String PARAM_COMMIT_ID = "commitId";
	private static final String PARAM_GIT_BRANCH_NAME = "branchName";
	private static final String PARAM_GIT_URL = "gitUrl";

	@Before
	public void setup() throws IOException, GeneralSecurityException {
		GoogleSpreadsheetWriter.createSheet(curProj.getName(), 
				curProj.getGitUrl(), 
				Arrays.asList(new String[] {"Git Commit Id", 
						"Analysis Time (s)", 
						"Memory Used (MB)", 
						"Soot Reachable Methods", 
						"#Rules", 
						"Number Of Seeds", 
						"Number Of Secure Objects",
						}));
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
			protected List<CryptSLRule> getRules() {
				return CrySLRulesetSelector.makeFromRuleset(IDEALCrossingTestingFramework.RULES_BASE_DIR, rulesets);
			}

			@Override
			protected String applicationClassPath() {
				return mp.getBuildDirectory();
			}


			@Override
			public CrySLAnalysisListener getAdditionalListener() {
				return new PerformanceReportListener(proj, commitId, getRules(), branchUrl);
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
			protected List<CryptSLRule> getRules() {
				return CrySLRulesetSelector.makeFromRuleset(IDEALCrossingTestingFramework.RULES_BASE_DIR, rulesets);
			}

			@Override
			protected String applicationClassPath() {
				return new File(proj.getProjectPath()).getAbsolutePath();
			}


			@Override
			protected CrySLAnalysisListener getAdditionalListener() {
				return new PerformanceReportListener(proj, commitId, getRules(), branchUrl);
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
				"https://github.com/bitpay/java-bitpay-client", 
				"", 
				true, 
				new Ruleset[] {Ruleset.JavaCryptographicArchitecture}
				);
		BenchmarkProject project2 = new BenchmarkProject("Aerogear-Crypto", 
				"../CryptoAnalysisTargets/PerformanceBenchmarkProjects/aerogear-crypto", 
				"https://github.com/aerogear/aerogear-crypto-java", 
				"", 
				true, 
				new Ruleset[] {Ruleset.JavaCryptographicArchitecture, Ruleset.BouncyCastle}
				);
		BenchmarkProject project3 = new BenchmarkProject("Commons-Crypto", 
				"../CryptoAnalysisTargets/PerformanceBenchmarkProjects/commons-crypto", 
				"https://github.com/apache/commons-crypto", 
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

	@SuppressWarnings("unused")
	@Test
	public void test() throws Exception {
		String gitCommitId = String.valueOf(System.currentTimeMillis()), branchName = "", gitUrl = "";
		if (System.getProperty(PARAM_COMMIT_ID) != null)
			gitCommitId = System.getProperty(PARAM_COMMIT_ID);
		
		if (System.getProperty(PARAM_GIT_BRANCH_NAME) != null)
			branchName = System.getProperty(PARAM_GIT_BRANCH_NAME);
		
		if (System.getProperty(PARAM_GIT_URL) != null)
			gitUrl = System.getProperty(PARAM_GIT_URL);
		
		if (curProj.getIsMavenProject()) {
			MavenProject mavenProject = createAndCompile(new File(curProj.getProjectPath()).getAbsolutePath());
			scanner = createScanner(mavenProject, curProj, gitCommitId, getCommitUrl(gitUrl, gitCommitId), curProj.getRuleSet());
		} else {
			scanner = createScanner(curProj, gitCommitId, getCommitUrl(gitUrl, gitCommitId), curProj.getRuleSet());
		}
		scanner.exec();
	}
}
