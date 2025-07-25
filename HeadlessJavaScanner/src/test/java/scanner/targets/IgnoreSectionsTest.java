/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package scanner.targets;

import crypto.analysis.errors.AlternativeReqPredicateError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import de.fraunhofer.iem.scanner.HeadlessJavaScanner;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import scanner.setup.AbstractHeadlessTest;
import scanner.setup.ErrorSpecification;
import scanner.setup.MavenProject;

public class IgnoreSectionsTest extends AbstractHeadlessTest {

    @Test
    public void ignoreNoPackages() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/IgnorePackagesExample").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        List<String> ignoredSections = Collections.emptyList();
        scanner.setIgnoredSections(ignoredSections);

        // No sections are ignored, i.e. all errors are be reported
        addErrorSpecification(
                new ErrorSpecification.Builder("example.ConstraintErrorExample", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.IncompleteOperationErrorExample", "doUpdate", 0)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.PredicateMissingExample", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(AlternativeReqPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.TypestateErrorExample", "main", 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void ignoreMethodsExample() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/IgnorePackagesExample").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        List<String> ignoredMethods =
                Arrays.asList(
                        "example.PredicateMissingExample.main",
                        "example.TypestateErrorExample.main");
        scanner.setIgnoredSections(ignoredMethods);

        addErrorSpecification(
                new ErrorSpecification.Builder("example.ConstraintErrorExample", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.IncompleteOperationErrorExample", "doUpdate", 0)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        // Errors are not reported because methods 'main' are ignored in these classes
        addErrorSpecification(
                new ErrorSpecification.Builder("example.PredicateMissingExample", "main", 1)
                        .withNoErrors(ConstraintError.class)
                        .withNoErrors(RequiredPredicateError.class)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.TypestateErrorExample", "main", 1)
                        .withNoErrors(TypestateError.class)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void ignoreClassesExample() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/IgnorePackagesExample").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        List<String> ignoredClasses =
                Arrays.asList(
                        "example.ConstraintErrorExample",
                        "example.IncompleteOperationErrorExample");
        scanner.setIgnoredSections(ignoredClasses);

        // Errors are not reported because classes 'ConstraintErrorExample' and
        // 'IncompleteOperationErrorExample' are ignored
        addErrorSpecification(
                new ErrorSpecification.Builder("example.ConstraintErrorExample", "main", 1)
                        .withNoErrors(ConstraintError.class)
                        .withNoErrors(IncompleteOperationError.class)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.IncompleteOperationErrorExample", "doUpdate", 0)
                        .withNoErrors(IncompleteOperationError.class)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.PredicateMissingExample", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(AlternativeReqPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.TypestateErrorExample", "main", 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void ignoreWildcardExample() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/IgnorePackagesExample").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        List<String> ignoredWildcards = Collections.singletonList("example.*");
        scanner.setIgnoredSections(ignoredWildcards);

        // No errors are reported because the package 'example' is ignored
        addErrorSpecification(
                new ErrorSpecification.Builder("example.ConstraintErrorExample", "main", 1)
                        .withNoErrors(ConstraintError.class)
                        .withNoErrors(IncompleteOperationError.class)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.IncompleteOperationErrorExample", "main", 1)
                        .withNoErrors(IncompleteOperationError.class)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.PredicateMissingExample", "main", 1)
                        .withNoErrors(ConstraintError.class)
                        .withNoErrors(RequiredPredicateError.class)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.TypestateErrorExample", "main", 1)
                        .withNoErrors(TypestateError.class)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }
}
