package tests.headless;

import boomerang.scene.Method;
import boomerang.scene.WrappedClass;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import crypto.HeadlessCryptoScanner;
import crypto.analysis.errors.AbstractError;
import crypto.utils.ErrorUtils;
import test.TestConstants;
import tests.headless.FindingsType.FalseNegatives;
import tests.headless.FindingsType.FalsePositives;
import tests.headless.FindingsType.NoFalseNegatives;
import tests.headless.FindingsType.NoFalsePositives;
import tests.headless.FindingsType.TruePositives;

import java.io.File;
import java.util.Set;

public abstract class AbstractHeadlessTest {

	/**
	 * To run these test cases in Eclipse, specify your maven home path as JVM argument: -Dmaven.home=<PATH_TO_MAVEN_BIN>
	 */

	private final Table<String, Class<?>, Integer> errorMarkerCountPerErrorTypeAndMethod = HashBasedTable.create();
	
	protected static MavenProject createAndCompile(String mavenProjectPath) {
		MavenProject mi = new MavenProject(mavenProjectPath);
		mi.compile();
		return mi;
	}

	protected static HeadlessCryptoScanner createScanner(MavenProject mp) {
		return createScanner(mp, TestConstants.JCA_RULESET_PATH);
	}

	protected static HeadlessCryptoScanner createScanner(MavenProject mp, String rulesetPath) {
		String applicationPath = mp.getBuildDirectory();

		HeadlessCryptoScanner scanner = new HeadlessCryptoScanner(applicationPath, rulesetPath);
		scanner.setSootClassPath(mp.getBuildDirectory() + (mp.getFullClassPath().isEmpty() ? "" : File.pathSeparator + mp.getFullClassPath()));

		return scanner;
	}

	protected void assertErrors(Table<WrappedClass, Method, Set<AbstractError>> errorCollection) {
		StringBuilder report = new StringBuilder();

		// Compare expected errors to actual errors
		for (Table.Cell<String, Class<?>, Integer> cell : errorMarkerCountPerErrorTypeAndMethod.cellSet()) {
			String methodName = cell.getRowKey();
			Class<?> errorType = cell.getColumnKey();

			int excepted = cell.getValue();
			int actual = ErrorUtils.getErrorsOfTypeInMethod(methodName, errorType, errorCollection);

			int difference = excepted - actual;
			if (difference < 0) {
				report.append("\n\tFound ").append(Math.abs(difference)).append(" too many errors of type ").append(errorType.getSimpleName()).append(" in method ").append(methodName);
			} else if (difference > 0) {
				report.append("\n\tFound ").append(difference).append(" too few errors of type ").append(errorType.getSimpleName()).append(" in method ").append(methodName);
			}
		}

		// Compare actual errors to unexpected errors
		for (Table.Cell<WrappedClass, Method, Set<AbstractError>> cell : errorCollection.cellSet()) {
			String methodName = cell.getColumnKey().toString();
			Set<AbstractError> errors = cell.getValue();

			for (AbstractError error : errors) {
				Class<?> errorType = error.getClass();
				if (errorMarkerCountPerErrorTypeAndMethod.contains(methodName, errorType)) {
					continue;
				}

				int unexpectedErrors = ErrorUtils.getErrorsOfType(errorType, errors);
				report.append("\n\tFound ").append(unexpectedErrors).append(" too many errors of type ").append(errorType.getSimpleName()).append(" in method ").append(methodName);
			}
		}

		if (!report.toString().isEmpty()) {
			throw new RuntimeException("Tests not executed as planned:" + report);
		}
	}

	protected void setErrorsCount(String methodSignature, Class<?> errorType, int errorMarkerCount) {
		if (errorMarkerCountPerErrorTypeAndMethod.contains(methodSignature, errorType)) {
			throw new RuntimeException("Error Type already specified for this method");
		}
		errorMarkerCountPerErrorTypeAndMethod.put(methodSignature, errorType, errorMarkerCount);
	}

	protected void setErrorsCount(Class<?> errorType, TruePositives tp, FalsePositives fp, FalseNegatives fn, String methodSignature) {
		if (errorMarkerCountPerErrorTypeAndMethod.contains(methodSignature, errorType)) {
			int errorCount = errorMarkerCountPerErrorTypeAndMethod.get(methodSignature, errorType);
			errorMarkerCountPerErrorTypeAndMethod.remove(methodSignature, errorType);
			errorMarkerCountPerErrorTypeAndMethod.put(methodSignature, errorType, tp.getNumberOfFindings() + fp.getNumberOfFindings() + errorCount);
		} else {
			errorMarkerCountPerErrorTypeAndMethod.put(methodSignature, errorType, tp.getNumberOfFindings() + fp.getNumberOfFindings());
		}
	}

	protected void setErrorsCount(Class<?> errorType, TruePositives tp, String methodSignature) {
		setErrorsCount(errorType, tp, new NoFalsePositives(), new NoFalseNegatives(), methodSignature);
	}

	protected void setErrorsCount(Class<?> errorType, TruePositives tp, FalseNegatives fn, String methodSignature) {
		setErrorsCount(errorType, tp, new NoFalsePositives(), fn, methodSignature);
	}

	protected void setErrorsCount(Class<?> errorType, FalsePositives fp, String methodSignature) {
		setErrorsCount(errorType, new TruePositives(0), fp, new NoFalseNegatives(), methodSignature);
	}

	protected void setErrorsCount(Class<?> errorType, FalseNegatives fn, String methodSignature) {
		setErrorsCount(errorType, new TruePositives(0), new NoFalsePositives(), fn, methodSignature);
	}

	protected void setErrorsCount(ErrorSpecification errorSpecification) {
		if (errorSpecification.getTotalNumberOfFindings() > 0) {
			for (TruePositives tp : errorSpecification.getTruePositives()) {
				setErrorsCount(tp.getErrorType(), tp, new NoFalsePositives(), new NoFalseNegatives(), errorSpecification.getMethodSignature());
			}
			for (FalsePositives fp : errorSpecification.getFalsePositives()) {
				setErrorsCount(fp.getErrorType(), new TruePositives(0), fp, new NoFalseNegatives(), errorSpecification.getMethodSignature());
			}
		}
	}
}
