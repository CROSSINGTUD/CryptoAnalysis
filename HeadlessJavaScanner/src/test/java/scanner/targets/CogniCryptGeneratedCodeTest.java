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
import crypto.analysis.errors.RequiredPredicateError;
import de.fraunhofer.iem.scanner.HeadlessJavaScanner;
import java.io.File;
import org.junit.jupiter.api.Test;
import scanner.setup.AbstractHeadlessTest;
import scanner.setup.ErrorSpecification;
import scanner.setup.MavenProject;

public class CogniCryptGeneratedCodeTest extends AbstractHeadlessTest {

    @Test
    public void fileEncryptor() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/FileEncryptor").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("Crypto.Enc", "encrypt", 2)
                        .withTPs(ConstraintError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("Crypto.KeyDeriv", "getKey", 1)
                        .withFPs(ConstraintError.class, 1, "Mystery")
                        .withFPs(RequiredPredicateError.class, 3, "Mystery")
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("Crypto.Enc", "encrypt", 2)
                        .withFPs(AlternativeReqPredicateError.class, 1, "Mystery")
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("Crypto.Enc", "decrypt", 2)
                        .withFPs(RequiredPredicateError.class, 1, "Mystery")
                        .withFPs(AlternativeReqPredicateError.class, 1, "Mystery")
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    @Test
    public void userAuthenticator() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/UserAuthenticator").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("Crypto.PWHasher", "verifyPWHash", 2)
                        .withTPs(RequiredPredicateError.class, 3)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("Crypto.PWHasher", "createPWHash", 1)
                        .withNoErrors(RequiredPredicateError.class)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }
}
