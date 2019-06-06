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
	private static final String COMMIT_ID_PARAM = "commitId";

	@Before
	public void setup() throws IOException, GeneralSecurityException {
		GoogleSpreadsheetWriter.createSheet(curProj.getName(), 
				curProj.getGitUrl(), 
				Arrays.asList(new String[] {"Git Commit Id", 
						"Analysis Time", 
						"Memory Used (MB)", 
						"Soot Reachable Methods", 
						"#Rules", 
						"Number Of Seeds", 
						"Number Of Secure Objects",
						"Average Seed Analysis Time", 
						"Average Boomerang Analysis Time"}));
	}

	protected MavenProject createAndCompile(String mavenProjectPath) {
		MavenProject mi = new MavenProject(mavenProjectPath);
		mi.compile();
		return mi;
	}

	@SuppressWarnings("static-access")
	protected HeadlessCryptoScanner createScanner(MavenProject mp, BenchmarkProject proj, String commitId) {
		G.v().reset();
		HeadlessCryptoScanner scanner = new HeadlessCryptoScanner() {
			@Override
			protected String sootClassPath() {
				return mp.getBuildDirectory()
						+ (mp.getFullClassPath().equals("") ? "" : File.pathSeparator + mp.getFullClassPath());
			}

			@Override
			protected List<CryptSLRule> getRules() {
				return CrySLRulesetSelector.makeFromRuleset(IDEALCrossingTestingFramework.RULES_BASE_DIR, proj.getRuleSet());
			}

			@Override
			protected String applicationClassPath() {
				return mp.getBuildDirectory();
			}


			@Override
			public CrySLAnalysisListener getAdditionalListener() {
				return new PerformanceReportListener(proj, commitId, getRules());
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
	protected HeadlessCryptoScanner createScanner(BenchmarkProject proj, String commitId) {
		G.v().reset();
		HeadlessCryptoScanner scanner = new HeadlessCryptoScanner() {
			@Override
			protected String sootClassPath() {
				return proj.getSootClassPath();
			}

			@Override
			protected List<CryptSLRule> getRules() {
				return CrySLRulesetSelector.makeFromRuleset(IDEALCrossingTestingFramework.RULES_BASE_DIR, proj.getRuleSet());
			}

			@Override
			protected String applicationClassPath() {
				return new File(proj.getProjectPath()).getAbsolutePath();
			}


			@Override
			protected CrySLAnalysisListener getAdditionalListener() {
				return new PerformanceReportListener(proj, commitId, getRules());
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
		BenchmarkProject project1 = new BenchmarkProject("CogniCryptDemoExample-1", 
				"../CryptoAnalysisTargets/PerformanceBenchmarkProjects/CogniCryptDemoExample", 
				"https://github.com/CROSSINGTUD/CryptoAnalysis/tree/master/CryptoAnalysisTargets/CogniCryptDemoExample", 
				"", 
				true, 
				Ruleset.JavaCryptographicArchitecture
				);
		BenchmarkProject project2 = new BenchmarkProject("CogniCryptDemoExample-2", 
				"../CryptoAnalysisTargets/PerformanceBenchmarkProjects/CogniCryptDemoExample", 
				"https://github.com/CROSSINGTUD/CryptoAnalysis/tree/master/CryptoAnalysisTargets/CogniCryptDemoExample", 
				"", 
				true, 
				Ruleset.JavaCryptographicArchitecture
				);
		params.add(new Object[] {project1});
		params.add(new Object[] {project2});
		return params;
	}

	public PerformanceTest(BenchmarkProject proj) {
		this.curProj = proj;
	}

	@Test
	public void test() throws Exception {
		String gitCommitId = "test-commit-id-"+System.currentTimeMillis();
		if (System.getProperty(COMMIT_ID_PARAM) != null)
			gitCommitId = System.getProperty(COMMIT_ID_PARAM);
		if (curProj.getIsMavenProject()) {
			MavenProject mavenProject = createAndCompile(new File(curProj.getProjectPath()).getAbsolutePath());
			scanner = createScanner(mavenProject, curProj, gitCommitId);
		} else {
			scanner = createScanner(curProj, gitCommitId);
		}
		scanner.exec();
	}
}
