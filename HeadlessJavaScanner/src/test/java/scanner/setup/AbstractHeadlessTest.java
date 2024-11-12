package scanner.setup;

import boomerang.scene.Method;
import boomerang.scene.WrappedClass;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import crypto.analysis.errors.AbstractError;
import de.fraunhofer.iem.scanner.HeadlessJavaScanner;
import de.fraunhofer.iem.scanner.ScannerSettings;
import org.junit.Assert;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * To run these test cases in Eclipse, specify your maven home path as JVM argument: -Dmaven.home=<PATH_TO_MAVEN_BIN>
 */
public abstract class AbstractHeadlessTest {

	private static final String SOOT = "SOOT";
	private static final String SOOT_UP = "SOOT_UP";
	private static final String OPAL = "OPAL";

	protected static final String RULES_BASE_DIR = "." + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "rules" + File.separator;

	protected static final String JCA_RULESET_PATH = RULES_BASE_DIR + "JavaCryptographicArchitecture" + File.separator;

	protected static final String BOUNCY_CASTLE_RULESET_PATH = RULES_BASE_DIR + "BouncyCastle" + File.separator;

	private final Table<MethodWrapper, Class<?>, Integer> errorMarkerCounts = HashBasedTable.create();

	protected static MavenProject createAndCompile(String mavenProjectPath) {
		MavenProject mi = new MavenProject(mavenProjectPath);
		mi.compile();
		return mi;
	}

	protected static HeadlessJavaScanner createScanner(MavenProject mp) {
		return createScanner(mp, JCA_RULESET_PATH);
	}

	protected static HeadlessJavaScanner createScanner(MavenProject mp, String rulesetPath) {
		String applicationPath = mp.getBuildDirectory();

		HeadlessJavaScanner scanner = new HeadlessJavaScanner(applicationPath, rulesetPath);
		scanner.setSootClassPath(mp.getBuildDirectory() + (mp.getFullClassPath().isEmpty() ? "" : File.pathSeparator + mp.getFullClassPath()));

		scanner.setFramework(getFramework());

		return scanner;
	}

	private static ScannerSettings.Framework getFramework() {
		String framework = System.getProperty("framework");

		if (SOOT.equals(framework)) {
			return ScannerSettings.Framework.SOOT;
		} else if (SOOT_UP.equals(framework)) {
			return ScannerSettings.Framework.SOOT_UP;
		} else if (OPAL.equals(framework)) {
			return ScannerSettings.Framework.OPAL;
		} else {
			return ScannerSettings.Framework.SOOT;
		}
	}

	protected final void addErrorSpecification(ErrorSpecification spec) {
		MethodWrapper wrapper = spec.getMethodWrapper();

		for (Map.Entry<Class<?>, Integer> entry : spec.getFindings().entrySet()) {
			if (errorMarkerCounts.contains(wrapper, entry.getKey())) {
				throw new RuntimeException("Error Type cannot be specified multiple times for the same method");
			}

			errorMarkerCounts.put(wrapper, entry.getKey(), entry.getValue());
		}
	}

	protected final void assertErrors(Table<WrappedClass, Method, Set<AbstractError>> collectedErrors) {
		StringBuilder report = new StringBuilder();

		// Assert True Positives and False Positives
		for (Table.Cell<MethodWrapper, Class<?>, Integer> cell : errorMarkerCounts.cellSet()) {
			MethodWrapper methodWrapper = cell.getRowKey();
			Class<?> errorType = cell.getColumnKey();

			int expected = cell.getValue();
			int actual = getErrorsOfTypeInMethod(methodWrapper, errorType, collectedErrors);

			int difference = expected - actual;
			if (difference < 0) {
				report.append("\n\tFound ").append(Math.abs(difference)).append(" too many errors of type ").append(errorType.getSimpleName()).append(" in ").append(methodWrapper);
			} else if (difference > 0) {
				report.append("\n\tFound ").append(difference).append(" too few errors of type ").append(errorType.getSimpleName()).append(" in ").append(methodWrapper);
			}
		}

		// Assert False Negatives
		for (Table.Cell<WrappedClass, Method, Set<AbstractError>> cell : collectedErrors.cellSet()) {
			Method method = cell.getColumnKey();
			MethodWrapper methodWrapper = new MethodWrapper(method.getDeclaringClass().getName(), method.getName(), method.getParameterTypes().size());
			Set<AbstractError> errors = cell.getValue();

			for (AbstractError error : errors) {
				Class<?> errorType = error.getClass();
				if (errorMarkerCounts.contains(methodWrapper, errorType)) {
					continue;
				}

				int unexpectedErrors = getErrorsOfType(errorType, errors);
				report.append("\n\tFound ").append(unexpectedErrors).append(" too many errors of type ").append(errorType.getSimpleName()).append(" in ").append(methodWrapper);
			}
		}

		if (!report.toString().isEmpty()) {
			Assert.fail("Tests not executed as planned:" + report);
		}
	}

	private int getErrorsOfTypeInMethod(MethodWrapper methodWrapper, Class<?> errorClass, Table<WrappedClass, Method, Set<AbstractError>> errorCollection) {
		int result = 0;

		for (Table.Cell<WrappedClass, Method, Set<AbstractError>> cell : errorCollection.cellSet()) {
			Method method = cell.getColumnKey();
			MethodWrapper collectedMethodWrapper = new MethodWrapper(method.getDeclaringClass().getName(), method.getName(), method.getParameterTypes().size());

			if (!collectedMethodWrapper.equals(methodWrapper)) {
				continue;
			}

			for (AbstractError error : cell.getValue()) {
				String errorName = error.getClass().getSimpleName();

				if (errorName.equals(errorClass.getSimpleName())) {
					result++;
				}
			}
		}

		return result;
	}

	private int getErrorsOfType(Class<?> errorType, Collection<AbstractError> errors) {
		int result = 0;

		for (AbstractError error : errors) {
			if (error.getClass().getSimpleName().equals(errorType.getSimpleName())) {
				result++;
			}
		}

		return result;
	}
}
