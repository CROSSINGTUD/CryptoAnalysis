/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package android;

import boomerang.scope.Method;
import boomerang.scope.WrappedClass;
import com.google.common.collect.Table;
import crypto.analysis.errors.AbstractError;
import de.fraunhofer.iem.android.HeadlessAndroidScanner;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Assertions;

/**
 * Running the tests requires an Android platform. Since they are licensed and quite large, they
 * should not be uploaded to the GitHub remote. If you plan to run the tests, copy a platform (e.g.
 * 'android-35') into the "src/test/resources/platforms/" directory. The files inside this directory
 * are ignored for GitHub.
 */
public abstract class AbstractAndroidTest {

    protected static final String APK_PATH =
            "."
                    + File.separator
                    + "src"
                    + File.separator
                    + "test"
                    + File.separator
                    + "resources"
                    + File.separator
                    + "apk"
                    + File.separator;
    protected static final String PLATFORMS_PATH =
            "."
                    + File.separator
                    + "src"
                    + File.separator
                    + "test"
                    + File.separator
                    + "resources"
                    + File.separator
                    + "platforms"
                    + File.separator;
    protected static final String JCA_RULES_DIR =
            "."
                    + File.separator
                    + "src"
                    + File.separator
                    + "test"
                    + File.separator
                    + "resources"
                    + File.separator
                    + "rules"
                    + File.separator
                    + "JavaCryptographicArchitecture"
                    + File.separator;

    private final Map<Class<?>, Integer> expectedErrors = new HashMap<>();

    protected final HeadlessAndroidScanner createScanner(String apkFileName) {
        String apkFile = APK_PATH + apkFileName;

        return new HeadlessAndroidScanner(apkFile, PLATFORMS_PATH, JCA_RULES_DIR);
    }

    protected final void addExpectedErrors(Class<?> errorType, int numberOfFindings) {
        if (expectedErrors.containsKey(errorType)) {
            throw new RuntimeException("Error type cannot be configured multiple times");
        }
        expectedErrors.put(errorType, numberOfFindings);
    }

    protected final void assertErrors(
            Table<WrappedClass, Method, Set<AbstractError>> errorCollection) {
        Set<String> report = new HashSet<>();

        for (Map.Entry<Class<?>, Integer> entry : expectedErrors.entrySet()) {
            Class<?> errorType = entry.getKey();

            int expected = entry.getValue();
            int actual = getErrorsOfType(errorType, errorCollection);

            int difference = expected - actual;
            if (difference < 0) {
                report.add(
                        "Found "
                                + Math.abs(difference)
                                + " too many errors of type "
                                + errorType.getSimpleName());
            } else if (difference > 0) {
                report.add(
                        "Found "
                                + difference
                                + " too few errors of type "
                                + errorType.getSimpleName());
            }
        }

        for (Set<AbstractError> errors : errorCollection.values()) {
            for (AbstractError error : errors) {
                Class<?> errorType = error.getClass();

                if (expectedErrors.containsKey(errorType)) {
                    continue;
                }

                int unexpectedErrors = getErrorsOfType(errorType, errorCollection);
                report.add(
                        "Found "
                                + unexpectedErrors
                                + " too many errors of type "
                                + errorType.getSimpleName());
            }
        }

        if (!report.isEmpty()) {
            Assertions.fail("Tests not executed as planned:\n\t" + String.join("\n\t", report));
        }
    }

    private int getErrorsOfType(
            Class<?> errorClass, Table<WrappedClass, Method, Set<AbstractError>> errorCollection) {
        int result = 0;

        for (Set<AbstractError> errors : errorCollection.values()) {
            for (AbstractError error : errors) {
                String errorName = error.getClass().getSimpleName();

                if (errorName.equals(errorClass.getSimpleName())) {
                    result++;
                }
            }
        }

        return result;
    }
}
