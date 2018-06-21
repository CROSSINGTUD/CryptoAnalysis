package test.headless;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

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
import crypto.analysis.EnsuredCryptSLPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.NeverTypeOfError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLPredicate;
import sync.pds.solver.nodes.Node;
import test.IDEALCrossingTestingFramework;
import typestate.TransitionFunction;

public class HeadlessTests {
	
	private static boolean VISUALIZATION = false;
	private CrySLAnalysisListener errorCountingAnalysisListener;
	private Table<String, Class<?>, Integer> errorMarkerCountPerErrorTypeAndMethod = HashBasedTable.create();

	@Test
	public void cogniCryptDemoExamples() {
		String sootClassPath = new File("../CryptoAnalysisTargets/CogniCryptDemoExample/bin").getAbsolutePath();
		HeadlessCryptoScanner scanner = createAnalysisFor(sootClassPath, sootClassPath);

		setErrorsCount("<example.ConstraintErrorExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		
		setErrorsCount("<example.PredicateMissingExample: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<example.PredicateMissingExample: void main(java.lang.String[])>", ConstraintError.class, 1);
		
		setErrorsCount("<example.TypestateErrorExample: void main(java.lang.String[])>", TypestateError.class, 1);

		setErrorsCount("<example.IncompleOperationErrorExample: void main(java.lang.String[])>", IncompleteOperationError.class, 2);
		
		setErrorsCount("<example.ImpreciseValueExtractionErrorExample: void main(java.lang.String[])>", ImpreciseValueExtractionError.class, 2);

		scanner.exec();
		assertErrors();
	}
	
	@Test
	public void oracleExample() {
		String sootClassPath = new File("../CryptoAnalysisTargets/OracleExample/bin").getAbsolutePath();
		HeadlessCryptoScanner scanner = createAnalysisFor(sootClassPath, sootClassPath);
//
		setErrorsCount("<main.Main: void main(java.lang.String[])>", ConstraintError.class, 1);
		setErrorsCount("<main.Main: void main(java.lang.String[])>", TypestateError.class, 1);
		setErrorsCount("<main.Main: void main(java.lang.String[])>", RequiredPredicateError.class, 1);
		setErrorsCount("<main.Main: void keyStoreExample()>", NeverTypeOfError.class, 1);
		setErrorsCount("<main.Main: void cipherUsageExample()>", ConstraintError.class, 1);
		
		setErrorsCount("<main.Main: void use(javax.crypto.Cipher)>", TypestateError.class, 1);


		//TODO this is a spurious finding. What happens here?
		setErrorsCount("<Crypto.PWHasher: java.lang.Boolean verifyPWHash(char[],java.lang.String)>", RequiredPredicateError.class, 1);
		

		setErrorsCount("<main.Main: void incorrectKeyForWrongCipher()>", ConstraintError.class, 1);
		setErrorsCount("<main.Main: void incorrectKeyForWrongCipher()>", RequiredPredicateError.class, 1);

		scanner.exec();
		assertErrors();
	}

	@Test
	public void stopwatchExample() {
		String applicationClassPath = new File("../CryptoAnalysisTargets/StopwatchExample/bin").getAbsolutePath();
		String sootClassPath = applicationClassPath + File.pathSeparator
				+ new File("../CryptoAnalysisTargets/StopwatchExample/guava-23.0.jar").getAbsolutePath();
		String rulesDir = new File("../CryptoAnalysisTargets/StopwatchExample/rules").getAbsolutePath();
		HeadlessCryptoScanner scanner = createAnalysisFor(applicationClassPath, sootClassPath, rulesDir);
		//TODO this is wrong. The state machine does not label the correct accepting states for the state machine.
		setErrorsCount("<main.Main: void correct()>", IncompleteOperationError.class, 2);
		setErrorsCount("<main.Main: void wrong()>", TypestateError.class, 1);
		setErrorsCount("<main.Main: void context(com.google.common.base.Stopwatch)>", TypestateError.class, 1);
		setErrorsCount("<main.Main: void context2(com.google.common.base.Stopwatch)>", TypestateError.class, 1);
		scanner.exec();
		assertErrors();

	}
	
	@Test
	public void cryptoMisuseExampleProject() {
		String sootClassPath = new File("../CryptoAnalysisTargets/CryptoMisuseExamples/bin").getAbsolutePath();
		HeadlessCryptoScanner scanner = createAnalysisFor(sootClassPath, sootClassPath);


		setErrorsCount("<main.Msg: byte[] sign(java.lang.String)>", ConstraintError.class, 1);
		setErrorsCount("<main.Msg: byte[] sign(java.lang.String)>", RequiredPredicateError.class, 1);
		setErrorsCount("<main.Msg: java.security.PrivateKey getPrivateKey()>", ConstraintError.class, 1);
		
		setErrorsCount("<main.Msg: void encryptAlgFromField()>", ConstraintError.class, 1);
		setErrorsCount("<main.Msg: void encrypt()>", ConstraintError.class, 1);
		setErrorsCount("<main.Msg: void encryptAlgFromVar()>", ConstraintError.class, 1);
		
		scanner.exec();
		assertErrors();
	}

	@Test
	public void stopwatchPathExpressionExample() {
		String applicationClassPath = new File("../CryptoAnalysisTargets/StopwatchPathExpression/bin").getAbsolutePath();
		String sootClassPath = applicationClassPath + File.pathSeparator
				+ new File("../CryptoAnalysisTargets/StopwatchPathExpression/lib/guava-23.0.jar").getAbsolutePath();
		String rulesDir = new File("../CryptoAnalysisTargets/StopwatchPathExpression/rules").getAbsolutePath();
		HeadlessCryptoScanner scanner = createAnalysisFor(applicationClassPath, sootClassPath, rulesDir);
		setErrorsCount("<pathexpression.PathExpressionComputer: pathexpression.IRegEx getExpressionBetween(java.lang.Object,java.lang.Object)>", TypestateError.class, 1);
		setErrorsCount("<pathexpression.DepthFirstSearchMain: void dfsFrom(test.IntGraph,int,java.util.Set,com.google.common.base.Stopwatch)>", TypestateError.class, 1);
		
		//TODO this is wrong. The state machine does not label the correct accepting states for the state machine.
		setErrorsCount("<pathexpression.Main: void main(java.lang.String[])>", IncompleteOperationError.class, 1);
		scanner.exec();
		assertErrors();

	}


	@Test
	public void glassfishExample() {
		String sootClassPath = new File("../CryptoAnalysisTargets/glassfish-embedded/bin").getAbsolutePath();
		HeadlessCryptoScanner scanner = createAnalysisFor(sootClassPath, sootClassPath);

		setErrorsCount("<org.glassfish.grizzly.config.ssl.CustomClass: void init(javax.crypto.SecretKey,java.lang.String)>", ConstraintError.class, 1);
		setErrorsCount("<org.glassfish.grizzly.config.ssl.JSSESocketFactory: java.security.KeyStore getStore(java.lang.String,java.lang.String,java.lang.String)>", NeverTypeOfError.class, 1);

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
				System.out.println(error.getErrorLocation().getMethod() + "  "+error);
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
