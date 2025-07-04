/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package scanner.setup;

import boomerang.scope.Method;
import boomerang.scope.WrappedClass;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import crypto.analysis.errors.AbstractError;
import de.fraunhofer.iem.scanner.HeadlessJavaScanner;
import de.fraunhofer.iem.scanner.ScannerSettings;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Assertions;

/**
 * To run these test cases in Eclipse, specify your maven home path as JVM argument:
 * -Dmaven.home=<PATH_TO_MAVEN_BIN>
 */
public abstract class AbstractHeadlessTest {

    private static final String SOOT = "soot";
    private static final String SOOT_UP = "sootup";
    private static final String OPAL = "opal";

    /** Use this variable to configure the framework when running the tests locally */
    private static final String LOCAL_TEST_FRAMEWORK = OPAL;

    protected static final String RULES_BASE_DIR =
            "."
                    + File.separator
                    + "src"
                    + File.separator
                    + "test"
                    + File.separator
                    + "resources"
                    + File.separator
                    + "rules"
                    + File.separator;

    protected static final String JCA_RULESET_PATH =
            RULES_BASE_DIR + "JavaCryptographicArchitecture" + File.separator;

    protected static final String BOUNCY_CASTLE_RULESET_PATH =
            RULES_BASE_DIR + "BouncyCastle" + File.separator;

    private final Table<MethodWrapper, Class<?>, Integer> errorMarkerCounts =
            HashBasedTable.create();

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
        scanner.setAddClassPath(
                mp.getBuildDirectory()
                        + (mp.getFullClassPath().isEmpty()
                                ? ""
                                : File.pathSeparator + mp.getFullClassPath()));

        scanner.setFramework(getFramework());

        return scanner;
    }

    private static ScannerSettings.Framework getFramework() {
        String framework = System.getProperty("testSetup", LOCAL_TEST_FRAMEWORK);

        switch (framework.toLowerCase()) {
            case SOOT -> {
                return ScannerSettings.Framework.SOOT;
            }
            case SOOT_UP -> {
                return ScannerSettings.Framework.SOOT_UP;
            }
            case OPAL -> {
                return ScannerSettings.Framework.OPAL;
            }
            default ->
                    throw new IllegalArgumentException(
                            "Cannot run tests with test setup " + framework);
        }
    }

    protected final void addErrorSpecification(ErrorSpecification spec) {
        MethodWrapper wrapper = spec.getMethodWrapper();

        for (Map.Entry<Class<?>, Integer> entry : spec.getFindings().entrySet()) {
            if (errorMarkerCounts.contains(wrapper, entry.getKey())) {
                throw new RuntimeException(
                        "Error Type cannot be specified multiple times for the same method");
            }

            errorMarkerCounts.put(wrapper, entry.getKey(), entry.getValue());
        }
    }

    protected final void assertErrors(
            Table<WrappedClass, Method, Set<AbstractError>> collectedErrors) {
        Set<String> report = new HashSet<>();

        // Assert True Positives and False Positives
        for (Table.Cell<MethodWrapper, Class<?>, Integer> cell : errorMarkerCounts.cellSet()) {
            MethodWrapper methodWrapper = cell.getRowKey();
            Class<?> errorType = cell.getColumnKey();

            int expected = cell.getValue();
            int actual = getErrorsOfTypeInMethod(methodWrapper, errorType, collectedErrors);

            int difference = expected - actual;
            if (difference < 0) {
                report.add(
                        "Found "
                                + Math.abs(difference)
                                + " too many errors of type "
                                + errorType.getSimpleName()
                                + " in "
                                + methodWrapper);
            } else if (difference > 0) {
                report.add(
                        "Found "
                                + difference
                                + " too few errors of type "
                                + errorType.getSimpleName()
                                + " in "
                                + methodWrapper);
            }
        }

        // Assert False Negatives
        for (Table.Cell<WrappedClass, Method, Set<AbstractError>> cell :
                collectedErrors.cellSet()) {
            Method method = cell.getColumnKey();
            MethodWrapper methodWrapper =
                    new MethodWrapper(
                            method.getDeclaringClass().getFullyQualifiedName(),
                            method.getName(),
                            method.getParameterTypes().size());
            Set<AbstractError> errors = cell.getValue();

            for (AbstractError error : errors) {
                Class<?> errorType = error.getClass();
                if (errorMarkerCounts.contains(methodWrapper, errorType)) {
                    continue;
                }

                int unexpectedErrors = getErrorsOfType(errorType, errors);
                report.add(
                        "Found "
                                + unexpectedErrors
                                + " too many errors of type "
                                + errorType.getSimpleName()
                                + " in "
                                + methodWrapper);
            }
        }

        if (!report.isEmpty()) {
            Assertions.fail("Tests not executed as planned:\n\t" + String.join("\n\t", report));
        }
    }

    private int getErrorsOfTypeInMethod(
            MethodWrapper methodWrapper,
            Class<?> errorClass,
            Table<WrappedClass, Method, Set<AbstractError>> errorCollection) {
        int result = 0;

        for (Table.Cell<WrappedClass, Method, Set<AbstractError>> cell :
                errorCollection.cellSet()) {
            Method method = cell.getColumnKey();
            MethodWrapper collectedMethodWrapper =
                    new MethodWrapper(
                            method.getDeclaringClass().getFullyQualifiedName(),
                            method.getName(),
                            method.getParameterTypes().size());

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
