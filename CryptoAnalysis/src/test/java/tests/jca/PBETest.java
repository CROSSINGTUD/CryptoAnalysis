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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.Ruleset;
import test.TestRules;
import test.TestRunnerInterceptor;
import test.assertions.Assertions;

@ExtendWith(TestRunnerInterceptor.class)
@Ruleset(TestRules.JCA)
public class PBETest {

    @Test
    public void predictablePassword() {
        char[] defaultKey = new char[] {'s', 'a', 'a', 'g', 'a', 'r'};
        byte[] salt = new byte[16];
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(salt);
        PBEKeySpec pbeKeySpec = new PBEKeySpec(defaultKey, salt, 11010, 16);
        Assertions.notHasEnsuredPredicate(pbeKeySpec);
        pbeKeySpec.clearPassword();
        Assertions.mustBeInAcceptingState(pbeKeySpec);
    }

    @Test
    public void unPredictablePassword() {
        char[] defaultKey = generateRandomPassword();
        byte[] salt = new byte[16];
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(salt);

        PBEKeySpec pbeKeySpec = new PBEKeySpec(defaultKey, salt, 11010, 16);
        Assertions.hasEnsuredPredicate(pbeKeySpec);
        pbeKeySpec.clearPassword();
        Assertions.mustBeInAcceptingState(pbeKeySpec);
    }

    @Test
    public void pbeUsagePatternMinPBEIterationsMinimized() throws GeneralSecurityException {
        final byte[] salt = new byte[32];
        SecureRandom.getInstanceStrong().nextBytes(salt);

        char[] corPwd = generateRandomPassword();
        PBEKeySpec pbekeyspec = new PBEKeySpec(corPwd, salt, 100000, 128);
        Assertions.extValue(1);
        Assertions.hasEnsuredPredicate(pbekeyspec);
    }

    @Test
    public void pbeUsagePatternMinPBEIterations() throws GeneralSecurityException {
        final byte[] salt = new byte[32];
        SecureRandom.getInstanceStrong().nextBytes(salt);

        char[] corPwd = generateRandomPassword();
        PBEKeySpec pbekeyspec = new PBEKeySpec(corPwd, salt, 100000, 128);
        Assertions.extValue(1);
        Assertions.extValue(2);
        Assertions.extValue(3);
        Assertions.hasEnsuredPredicate(pbekeyspec);
        Assertions.mustNotBeInAcceptingState(pbekeyspec);
        pbekeyspec.clearPassword();
        Assertions.mustBeInAcceptingState(pbekeyspec);
        pbekeyspec = new PBEKeySpec(corPwd, salt, 9999, 128);
        Assertions.extValue(1);
        Assertions.extValue(2);
        Assertions.extValue(3);
        Assertions.notHasEnsuredPredicate(pbekeyspec);
        Assertions.mustNotBeInAcceptingState(pbekeyspec);
        pbekeyspec.clearPassword();
        Assertions.mustBeInAcceptingState(pbekeyspec);

        PBEParameterSpec pbeParSpec1 = new PBEParameterSpec(salt, 10000);
        Assertions.extValue(0);
        Assertions.extValue(1);
        Assertions.mustBeInAcceptingState(pbeParSpec1);
        Assertions.hasEnsuredPredicate(pbeParSpec1);

        PBEParameterSpec pbeParSpec2 = new PBEParameterSpec(salt, 9999);
        Assertions.extValue(0);
        Assertions.extValue(1);
        Assertions.notHasEnsuredPredicate(pbeParSpec2);
        Assertions.mustBeInAcceptingState(pbeParSpec2);
    }

    @Test
    public void pbeUsagePattern1() throws GeneralSecurityException {
        final byte[] salt = new byte[32];
        SecureRandom.getInstanceStrong().nextBytes(salt);

        Assertions.hasEnsuredPredicate(salt);
        char[] corPwd = generateRandomPassword();
        final PBEKeySpec pbekeyspec = new PBEKeySpec(corPwd, salt, 65000, 128);
        Assertions.extValue(1);
        Assertions.extValue(2);
        Assertions.extValue(3);
        Assertions.hasEnsuredPredicate(pbekeyspec);
        Assertions.mustNotBeInAcceptingState(pbekeyspec);
        pbekeyspec.clearPassword();
    }

    @Test
    public void pbeUsagePattern2() throws GeneralSecurityException {
        final byte[] salt = new byte[32];
        SecureRandom.getInstanceStrong().nextBytes(salt);
        Assertions.hasEnsuredPredicate(salt);
        final PBEKeySpec pbekeyspec = new PBEKeySpec(generateRandomPassword(), salt, 65000, 128);
        Assertions.extValue(2);
        Assertions.extValue(3);
        Assertions.mustNotBeInAcceptingState(pbekeyspec);
        Assertions.hasEnsuredPredicate(pbekeyspec, "speccedKey");

        pbekeyspec.clearPassword();
        Assertions.mustBeInAcceptingState(pbekeyspec);
        Assertions.notHasEnsuredPredicate(pbekeyspec, "speccedKey");
    }

    public char[] generateRandomPassword() {
        SecureRandom rnd = new SecureRandom();

        return IntStream.generate(() -> rnd.nextInt('a', 'z'))
                .mapToObj(Character::toString)
                .limit(10)
                .collect(Collectors.joining())
                .toCharArray();
    }

    @Test
    public void pbeUsagePatternForbiddenMeth() {
        char[] falsePwd = "password".toCharArray();
        final PBEKeySpec pbekeyspec = new PBEKeySpec(falsePwd);
        Assertions.callToForbiddenMethod();
        Assertions.notHasEnsuredPredicate(pbekeyspec);
    }
}
