/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package tests.tink;

import org.junit.jupiter.api.Disabled;
import test.UsagePatternTestingFramework;

@Disabled
public abstract class TestTinkPrimitives extends UsagePatternTestingFramework {
    /*@Override
    protected String getSootClassPath() {
        String sootCp = super.getSootClassPath();
        String userHome = System.getProperty("user.home");

        sootCp +=
                File.pathSeparator
                        + userHome
                        + "/.m2/repository/com/google/crypto/tink/tink/1.2.0/tink-1.2.0.jar";
        return sootCp;
    }*/
}
