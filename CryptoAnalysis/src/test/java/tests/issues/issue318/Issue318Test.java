/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package tests.issues.issue318;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.Ruleset;
import test.TestRules;
import test.TestRunnerInterceptor;
import test.assertions.Assertions;

@ExtendWith(TestRunnerInterceptor.class)
@Ruleset(TestRules.ISSUE_318)
public class Issue318Test {

    @Test
    public void testIssue318() {
        First f = new First();
        Assertions.notHasEnsuredPredicate(f);

        Second s = new Second(f);
        Assertions.notHasEnsuredPredicate(s);

        f.read();
        Assertions.hasEnsuredPredicate(f);
        s.goOn();
        Assertions.notHasEnsuredPredicate(s);

        Assertions.predicateErrors(1);
    }
}
