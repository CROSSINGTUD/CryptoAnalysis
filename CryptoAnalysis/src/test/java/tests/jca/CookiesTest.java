/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package tests.jca;

import javax.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.Ruleset;
import test.TestRules;
import test.TestRunnerInterceptor;
import test.assertions.Assertions;

@ExtendWith(TestRunnerInterceptor.class)
@Ruleset(TestRules.JCA)
public class CookiesTest {

    @Test
    public void testOne() {
        Cookie ck = new Cookie("name", "testing");
        ck.setSecure(true); // constraint is satisfied
        Assertions.hasEnsuredPredicate(ck);
        Assertions.mustBeInAcceptingState(ck);
    }

    @Test
    public void testTwo() {
        Cookie ck = new Cookie("name", "testing");
        ck.setSecure(false); // constraint is violated
        Assertions.notHasEnsuredPredicate(ck);
        Assertions.mustBeInAcceptingState(ck);
    }

    @Test
    public void testThree() {
        Cookie ck = new Cookie("name", "testing");
        // setSecure call is unused
        Assertions.notHasEnsuredPredicate(ck);
        Assertions.mustNotBeInAcceptingState(ck);
    }
}
