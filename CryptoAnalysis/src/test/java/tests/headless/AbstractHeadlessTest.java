package tests.headless;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Before;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import boomerang.BackwardQuery;
import boomerang.Query;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import boomerang.results.ForwardBoomerangResults;
import crypto.HeadlessCryptoScanner;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.CrySLAnalysisListener;
import crypto.analysis.CrySLRulesetSelector;
import crypto.analysis.CrySLRulesetSelector.Ruleset;
import crypto.analysis.EnsuredCryptSLPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLRule;
import soot.G;
import sync.pds.solver.nodes.Node;
import test.IDEALCrossingTestingFramework;
import typestate.TransitionFunction;

public abstract class AbstractHeadlessTest {

	/**
	 * To run these test cases in Eclipse, specify your maven home path as JVM argument:
	 * -Dmaven.home=<PATH_TO_MAVEN_BIN>
	 */
	
	private static boolean VISUALIZATION = false;
	private CrySLAnalysisListener errorCountingAnalysisListener;
	private Table<String, Class<?>, Integer> errorMarkerCountPerErrorTypeAndMethod = HashBasedTable.create();


	protected MavenProject createAndCompile(String mavenProjectPath) {
		MavenProject mi = new MavenProject(mavenProjectPath);
		mi.compile();
		return mi;
	}

	protected HeadlessCryptoScanner createScanner(MavenProject mp) {
		return createScanner(mp, Ruleset.JavaCryptographicArchitecture);
	}
	
	protected HeadlessCryptoScanner createScanner(MavenProject mp, Ruleset ruleset) {
		G.v().reset();
		HeadlessCryptoScanner scanner = new HeadlessCryptoScanner() {
			@Override
			protected String sootClassPath() {
				return mp.getBuildDirectory() +(mp.getFullClassPath().equals("") ? "": File.pathSeparator+ mp.getFullClassPath());
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
			protected CrySLAnalysisListener getAdditionalListener() {
				return errorCountingAnalysisListener;
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

	@Before
	public void setup() {
		errorCountingAnalysisListener = new CrySLAnalysisListener() {
			@Override
			public void reportError(AbstractError error) {
				Integer currCount;
				if(!errorMarkerCountPerErrorTypeAndMethod
						.contains(error.getErrorLocation().getMethod().toString(), error.getClass())) {
					currCount = 0;
				} else {
					currCount = errorMarkerCountPerErrorTypeAndMethod
							.get(error.getErrorLocation().getMethod().toString(), error.getClass());
				}
				Integer newCount = --currCount;
				errorMarkerCountPerErrorTypeAndMethod.put(error.getErrorLocation().getMethod().toString(),
						error.getClass(), newCount);
			}

			@Override
			public void onSeedTimeout(Node<Statement, Val> seed) {
			}

			@Override
			public void onSeedFinished(IAnalysisSeed seed, ForwardBoomerangResults<TransitionFunction> solver) {
			}

			@Override
			public void ensuredPredicates(Table<Statement, Val, Set<EnsuredCryptSLPredicate>> existingPredicates,
					Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> expectedPredicates,
					Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> missingPredicates) {

			}

			@Override
			public void discoveredSeed(IAnalysisSeed curr) {

			}

			@Override
			public void collectedValues(AnalysisSeedWithSpecification seed,
					Multimap<CallSiteWithParamIndex, ExtractedValue> collectedValues) {
			}

			@Override
			public void checkedConstraints(AnalysisSeedWithSpecification analysisSeedWithSpecification,
					Collection<ISLConstraint> relConstraints) {
			}

			@Override
			public void seedStarted(IAnalysisSeed analysisSeedWithSpecification) {
			}

			@Override
			public void boomerangQueryStarted(Query seed, BackwardQuery q) {
			}

			@Override
			public void boomerangQueryFinished(Query seed, BackwardQuery q) {

			}

			@Override
			public void beforePredicateCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {
			}

			@Override
			public void beforeConstraintCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {
			}

			@Override
			public void beforeAnalysis() {
			}

			@Override
			public void afterPredicateCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {
			}

			@Override
			public void afterConstraintCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {
			}

			@Override
			public void afterAnalysis() {
			}

			@Override
			public void onSecureObjectFound(IAnalysisSeed analysisObject) {
				// TODO Auto-generated method stub
				
			}
		};
	}

	protected void assertErrors() {
		for (Cell<String, Class<?>, Integer> c : errorMarkerCountPerErrorTypeAndMethod.cellSet()) {
			if (c.getValue() != 0) {
				if (c.getValue() > 0) {
					throw new RuntimeException(
							"Did not find all errors of type " + c.getColumnKey() + " in method " + c.getRowKey());
				} else {
					throw new RuntimeException(
							"Found too many  errors of type " + c.getColumnKey() + " in method " + c.getRowKey());
				}
			}
		}
	}

	protected void setErrorsCount(String methodSignature, Class<?> errorType, int errorMarkerCount) {
		if (errorMarkerCountPerErrorTypeAndMethod.contains(methodSignature, errorType)) {
			throw new RuntimeException("Error Type already specified for this method");
		}
		errorMarkerCountPerErrorTypeAndMethod.put(methodSignature, errorType, errorMarkerCount);
	}
}
