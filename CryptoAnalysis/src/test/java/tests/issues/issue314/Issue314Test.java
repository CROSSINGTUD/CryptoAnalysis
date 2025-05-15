/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package tests.issues.issue314;

import org.junit.jupiter.api.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class Issue314Test extends UsagePatternTestingFramework {

    @Override
    protected String getRulesetPath() {
        return TestConstants.RULES_TEST_DIR + "issue314";
    }

    @Test
    public void testIssue314() {
        Thread thread = new Thread();
        thread.start();

        Assertions.incompleteOperationErrors(0);
    }
}
