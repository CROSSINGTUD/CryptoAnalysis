package test.headless;

import java.io.File;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import boomerang.BackwardQuery;
import boomerang.Query;
import boomerang.WeightedBoomerang;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.HeadlessCryptoScanner;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.CrySLAnalysisListener;
import crypto.analysis.EnsuredCryptSLPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLPredicate;
import crypto.typestate.CallSiteWithParamIndex;
import sync.pds.solver.nodes.Node;
import test.IDEALCrossingTestingFramework;
import typestate.TransitionFunction;

public class HeadlessTests {
	private CrySLAnalysisListener errorCountingAnalysisListener;
	private Table<String, Class<?>, Integer> errorMarkerCountPerErrorTypeAndMethod = HashBasedTable.create();

	@Test
	public void oracleExample() {
		String sootClassPath = new File("../CryptoAnalysisTargets/OracleExample/bin").getAbsolutePath();
		HeadlessCryptoScanner scanner = createAnalysisFor(sootClassPath, sootClassPath);

		setErrorsCount("<main.Main: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<main.Main: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<main.Main: void main(java.lang.String[])>", RequiredPredicateError.class, 1);

		scanner.exec();
		assertErrors();
	}

	@Test
	public void stopwatchExample() {
		String applicationClassPath = new File("../CryptoAnalysisTargets/StopwatchExample/bin").getAbsolutePath();
		String sootClassPath = applicationClassPath + ":"
				+ new File("../CryptoAnalysisTargets/StopwatchExample/guava-23.0.jar").getAbsolutePath();
		String rulesDir = new File("../CryptoAnalysisTargets/StopwatchExample/rules").getAbsolutePath();
		HeadlessCryptoScanner scanner = createAnalysisFor(applicationClassPath, sootClassPath, rulesDir);
		//TODO this is wrong. The state machine does not label the correct accepting states for the state machine.
		setErrorsCount("<main.Main: void correct()>", IncompleteOperationError.class, 2);
		setErrorsCount("<main.Main: void wrong()>", TypestateError.class, 1);
		setErrorsCount("<main.Main: void context(com.google.common.base.Stopwatch)>", TypestateError.class, 2);
		setErrorsCount("<main.Main: void wrongWithTwoContexts()>", TypestateError.class, 2);
		scanner.exec();
		assertErrors();

	}
	

	@Test
	public void stopwatchPathExpressionExample() {
		String applicationClassPath = new File("../CryptoAnalysisTargets/StopwatchPathExpression/bin").getAbsolutePath();
		String sootClassPath = applicationClassPath + ":"
				+ new File("../CryptoAnalysisTargets/StopwatchPathExpression/lib/guava-23.0.jar").getAbsolutePath();
		String rulesDir = new File("../CryptoAnalysisTargets/StopwatchPathExpression/rules").getAbsolutePath();
		HeadlessCryptoScanner scanner = createAnalysisFor(applicationClassPath, sootClassPath, rulesDir);
		setErrorsCount("<pathexpression.Main: void main(java.lang.String[])>", TypestateError.class, 1);
		
		//TODO this is wrong. The state machine does not label the correct accepting states for the state machine.
		setErrorsCount("<pathexpression.Main: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		scanner.exec();
		assertErrors();

	}

	private HeadlessCryptoScanner createAnalysisFor(String applicationClassPath, String sootClassPath) {
		return createAnalysisFor(applicationClassPath, sootClassPath,
				new File(IDEALCrossingTestingFramework.RESOURCE_PATH).getAbsolutePath());
	}

	private HeadlessCryptoScanner createAnalysisFor(String applicationClassPath, String sootClassPath,
			String rulesDir) {
		HeadlessCryptoScanner scanner = new HeadlessCryptoScanner() {
			@Override
			protected String getCSVOutputFile() {
				return null;
			}

			@Override
			protected String getRulesDirectory() {
				return rulesDir;
			}

			@Override
			protected String sootClassPath() {
				return sootClassPath;
			}

			@Override
			protected String applicationClassPath() {
				return applicationClassPath;
			}

			@Override
			protected String softwareIdentifier() {
				return "";
			}

			@Override
			protected String getOutputFile() {
				return null;
			}

			@Override
			protected CrySLAnalysisListener getAdditionalListener() {
				return errorCountingAnalysisListener;
			}
		};
		return scanner;
	}

	@Before
	public void setup() {
		errorCountingAnalysisListener = new CrySLAnalysisListener() {
			@Override
			public void unevaluableConstraint(AnalysisSeedWithSpecification seed, ISLConstraint con,
					Statement location) {
			}

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
			public void predicateContradiction(Node<Statement, Val> node,
					Entry<CryptSLPredicate, CryptSLPredicate> disPair) {
			}

			@Override
			public void onSeedTimeout(Node<Statement, Val> seed) {
			}

			@Override
			public void onSeedFinished(IAnalysisSeed seed, Table<Statement, Val, TransitionFunction> solver) {
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
					Multimap<CallSiteWithParamIndex, Statement> collectedValues) {
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
		};
	}

	private void assertErrors() {
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

	private void setErrorsCount(String methodSignature, Class<?> errorType, int errorMarkerCount) {
		if (errorMarkerCountPerErrorTypeAndMethod.contains(methodSignature, errorType)) {
			throw new RuntimeException("Error Type already specified for this method");
		}
		errorMarkerCountPerErrorTypeAndMethod.put(methodSignature, errorType, errorMarkerCount);
	}
}
