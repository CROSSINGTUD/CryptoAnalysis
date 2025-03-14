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
import org.junit.Test;
import scanner.setup.AbstractHeadlessTest;
import scanner.setup.ErrorSpecification;
import scanner.setup.MavenProject;

public class ReportedIssueTest extends AbstractHeadlessTest {

    @Test
    public void reportedIssues() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/ReportedIssues").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("issueseeds.Main", "main", 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("issue227.WrappedHasher", "hash", 0)
                        .withNoErrors(TypestateError.class)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "issue208.Issue208WithSingleEntryPoint", "encryptImpl", 0)
                        .withNoErrors(RequiredPredicateError.class)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "issue208.Issue208WithMultipleEntryPoints", "encryptImpl", 0)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("issue81.Encryption", "encrypt", 2)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(AlternativeReqPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("issue81.Encryption", "generateKey", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(IncompleteOperationError.class, 1)
                        .withTPs(RequiredPredicateError.class, 4)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("issue81.Main", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "issuecognicrypt210.CogniCryptSecretKeySpec", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 3)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("issue70.ClientProtocolDecoder", "decryptAES", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .withTPs(AlternativeReqPredicateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("issue69.Issue69", "encryptByPublicKey", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(IncompleteOperationError.class, 1)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(AlternativeReqPredicateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("issue68.AESCryptor", "getKey", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(IncompleteOperationError.class, 1)
                        .withTPs(RequiredPredicateError.class, 3)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("issue68.AESCryptor", "getFactory", 0)
                        .withTPs(ConstraintError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("issue68.AESCryptor", "encryptImpl", 1)
                        .withTPs(AlternativeReqPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("issue68.AESCryptor", "<init>", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("issue68.AESCryptor", "decryptImpl", 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(AlternativeReqPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "issue68.simplified.field.AESCryptor", "encryptImpl", 1)
                        .withNoErrors(RequiredPredicateError.class)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("issue49.Main", "getPrivateKey", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("issue49.Main", "sign", 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("issue103.Main", "main", 1)
                        .withTPs(RequiredPredicateError.class, 4)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("issue137.Program", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void issue271Test() {
        // Related to https://github.com/CROSSINGTUD/CryptoAnalysis/issues/271
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/KotlinExamples/Issue271").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("example.Issue271Java", "testFail", 1)
                        .withNoErrors(IncompleteOperationError.class)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.Issue271Java", "example.Issue271Java", 1)
                        .withTPs(IncompleteOperationError.class, 0)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.Issue271Kotlin", "testFail", 1)
                        .withNoErrors(IncompleteOperationError.class)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.Issue271Kotlin", "testOk", 1)
                        .withNoErrors(IncompleteOperationError.class)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void issue270Test() {
        // Related to https://github.com/CROSSINGTUD/CryptoAnalysis/issues/270
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/Bugfixes/issue270").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("example.Launcher", "<init>", 0)
                        .withTPs(ConstraintError.class, 1)
                        .build());

        // Must not throw NullPointerException in ConstraintSolver:init()!
        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }
}
