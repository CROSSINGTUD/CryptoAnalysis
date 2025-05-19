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

import de.fraunhofer.iem.scanner.HeadlessJavaScanner;
import java.io.File;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import scanner.setup.AbstractHeadlessTest;
import scanner.setup.MavenProject;

public class TLSRuleTest extends AbstractHeadlessTest {

    @Disabled
    @Test
    public void secureFileTransmitter() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/SecureFileTransmitter").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }
}
