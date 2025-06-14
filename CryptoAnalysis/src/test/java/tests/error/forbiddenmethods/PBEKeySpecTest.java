/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package tests.error.forbiddenmethods;

import javax.crypto.spec.PBEKeySpec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.Ruleset;
import test.TestRules;
import test.TestRunnerInterceptor;
import test.assertions.Assertions;

@ExtendWith(TestRunnerInterceptor.class)
@Ruleset(TestRules.JCA)
public class PBEKeySpecTest {

    @Test
    public void PBEKeySpecTest1() {
        PBEKeySpec pbe = new PBEKeySpec(new char[] {});
        Assertions.callToForbiddenMethod();

        pbe.clearPassword();
    }

    @Test
    public void PBEKeySpecTest2() {
        PBEKeySpec pbe = new PBEKeySpec(new char[] {}, new byte[1], 1000);
        Assertions.callToForbiddenMethod();

        pbe.clearPassword();
    }

    @Test
    public void PBEKeySpecTest3() {
        PBEKeySpec pbe = new PBEKeySpec(new char[] {}, new byte[1], 1000);
        Assertions.callToForbiddenMethod();

        pbe.clearPassword();
    }
}
