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

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.Ruleset;
import test.TestRules;
import test.TestRunnerInterceptor;
import test.assertions.Assertions;

@ExtendWith(TestRunnerInterceptor.class)
@Ruleset(TestRules.JCA)
public class SecureRandomTest {

    @Test
    public void corSeed() throws GeneralSecurityException {
        SecureRandom r3 = SecureRandom.getInstanceStrong();
        Assertions.hasEnsuredPredicate(r3);

        SecureRandom r4 = SecureRandom.getInstanceStrong();
        Assertions.hasEnsuredPredicate(r4);
        r4.setSeed(r3.nextInt());
    }

    @Test
    public void fixedSeed() throws GeneralSecurityException {
        final int fixedSeed = 10;
        SecureRandom r3 = SecureRandom.getInstanceStrong();
        r3.setSeed(fixedSeed);
        Assertions.notHasEnsuredPredicate(r3);

        SecureRandom r4 = SecureRandom.getInstanceStrong();
        r4.setSeed(r3.nextInt());
        Assertions.notHasEnsuredPredicate(r4);
    }

    @Test
    public void dynSeed() {
        SecureRandom srPrep = new SecureRandom();
        byte[] bytes = new byte[32];
        srPrep.nextBytes(bytes);
        Assertions.mustBeInAcceptingState(srPrep);
        Assertions.hasEnsuredPredicate(bytes);
        // sr.setSeed(456789L); // Non compliant

        SecureRandom sr = new SecureRandom();
        sr.setSeed(bytes);
        int v = sr.nextInt();
        // For some reason, Soot adds a statement Integer.valueOf(v) which cannot be handled
        // Assertions.hasEnsuredPredicate(v);
        Assertions.mustBeInAcceptingState(sr);
    }

    @Test
    public void staticSeed() {
        byte[] bytes = {(byte) 100, (byte) 200};
        SecureRandom sr = new SecureRandom();
        sr.setSeed(bytes);
        int v = sr.nextInt();
        Assertions.notHasEnsuredPredicate(v);
        Assertions.mustBeInAcceptingState(sr);
    }
}
