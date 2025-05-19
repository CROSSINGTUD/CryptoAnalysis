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

import crypto.analysis.errors.IncompleteOperationError;
import de.fraunhofer.iem.scanner.HeadlessJavaScanner;
import java.io.File;
import org.junit.jupiter.api.Test;
import scanner.setup.AbstractHeadlessTest;
import scanner.setup.ErrorSpecification;
import scanner.setup.MavenProject;

public class MessageDigestExampleTest extends AbstractHeadlessTest {

    @Test
    public void loadMessageDigestExample() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/MessageDigestExample").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "MessageDigestExample.MessageDigestExample.Main", "getSHA256", 1)
                        .withTPs(IncompleteOperationError.class, 2)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }
}
