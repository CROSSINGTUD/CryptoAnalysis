package scanner;

import boomerang.scene.Method;
import boomerang.scene.WrappedClass;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import crypto.analysis.errors.AbstractError;
import crypto.utils.ErrorUtils;
import de.fraunhofer.iem.scanner.HeadlessJavaScanner;
import scanner.FindingsType.FalseNegatives;
import scanner.FindingsType.FalsePositives;
import scanner.FindingsType.NoFalseNegatives;
import scanner.FindingsType.NoFalsePositives;
import scanner.FindingsType.TruePositives;
import org.junit.Assert;

import java.io.File;
import java.util.Set;

/**
 * To run these test cases in Eclipse, specify your maven home path as JVM argument: -Dmaven.home=<PATH_TO_MAVEN_BIN>
 */
public abstract class AbstractHeadlessTest {

    protected static final String RULES_BASE_DIR = "." + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "rules" + File.separator;

    protected static final String JCA_RULESET_PATH = RULES_BASE_DIR + "JavaCryptographicArchitecture" + File.separator;

    protected static final String BOUNCY_CASTLE_RULESET_PATH = RULES_BASE_DIR + "BouncyCastle" + File.separator;

    private final Table<String, Class<?>, Integer> errorMarkerCountPerErrorTypeAndMethod = HashBasedTable.create();

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
            Assert.fail("Tests not executed as planned:" + report);
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
