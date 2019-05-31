package tests.performance;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.BeforeClass;
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
	String projectPath, sootClassPath;
	HeadlessCryptoScanner scanner;
	Ruleset ruleSet;
	boolean isMavenProject;
	String statisticsFilePath;
	
	@BeforeClass
	public static void setup() throws IOException, GeneralSecurityException {
		GoogleSpreadsheetWriter.createSheet(Arrays.asList(new String[] {"AnalysisTime", "AverageSeedAnalysisTime", "AverageBoomerangAnalysisTime", "NumberOfSeeds", "NumberOfSecureObjects"}));
	}
	
	protected MavenProject createAndCompile(String mavenProjectPath) {
		MavenProject mi = new MavenProject(mavenProjectPath);
		mi.compile();
		return mi;
	}
	
	protected void recordStats() {
		
	}
	
	@SuppressWarnings("static-access")
	protected HeadlessCryptoScanner createScanner(MavenProject mp, Ruleset ruleset) {
		G.v().reset();
		HeadlessCryptoScanner scanner = new HeadlessCryptoScanner() {
			@Override
			protected String sootClassPath() {
				return mp.getBuildDirectory()
						+ (mp.getFullClassPath().equals("") ? "" : File.pathSeparator + mp.getFullClassPath());
			}

			@Override
			protected List<CryptSLRule> getRules() {
				return CrySLRulesetSelector.makeFromRuleset(IDEALCrossingTestingFramework.RULES_BASE_DIR, ruleset);
			}

			@Override
			protected String applicationClassPath() {
				return mp.getBuildDirectory();
			}


			@Override
			public CrySLAnalysisListener getAdditionalListener() {
				return new PerformanceReportListener(statisticsFilePath);
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
	protected HeadlessCryptoScanner createScanner(String applicationPath, Ruleset ruleset, String sootClassPath) {
		G.v().reset();
		HeadlessCryptoScanner scanner = new HeadlessCryptoScanner() {
			@Override
			protected String sootClassPath() {
				return sootClassPath;
			}

			@Override
			protected List<CryptSLRule> getRules() {
				return CrySLRulesetSelector.makeFromRuleset(IDEALCrossingTestingFramework.RULES_BASE_DIR, ruleset);
			}

			@Override
			protected String applicationClassPath() {
				return applicationPath;
			}


			@Override
			protected CrySLAnalysisListener getAdditionalListener() {
				return new PerformanceReportListener(statisticsFilePath);
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
		params.add(new Object[] {"../CryptoAnalysisTargets/PerformanceBenchmarkProjects/BouncyCastle/COSE", Ruleset.BouncyCastle, true, ""});
		params.add(new Object[] {"../CryptoAnalysisTargets/PerformanceBenchmarkProjects/CogniCryptDemoExample", Ruleset.JavaCryptographicArchitecture, true, ""});
		return params;
	}
	
	public PerformanceTest(String projectPath, Ruleset rSet, boolean isMvnProject, String sootCp) {
		this.projectPath = projectPath;
		this.ruleSet = rSet;
		this.isMavenProject = isMvnProject;
		this.sootClassPath = sootCp;
	}
	
	@Test
	public void test() throws Exception {
		if (isMavenProject) {
			MavenProject mavenProject = createAndCompile(new File(projectPath).getAbsolutePath());
			scanner = createScanner(mavenProject, ruleSet);
		} else {
			scanner = createScanner(new File(projectPath).getAbsolutePath(), ruleSet, sootClassPath);
		}
		scanner.exec();
		recordStats();
	}
}
