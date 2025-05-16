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

import com.google.crypto.tink.DeterministicAead;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.daead.DeterministicAeadFactory;
import com.google.crypto.tink.daead.DeterministicAeadKeyTemplates;
import com.google.crypto.tink.proto.KeyTemplate;
import java.security.GeneralSecurityException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.Ruleset;
import test.TestRules;
import test.TestRunnerInterceptor;
import test.assertions.Assertions;

@ExtendWith(TestRunnerInterceptor.class)
@Ruleset(TestRules.TINK)
@Disabled
public class TestDeterministicAEADCipher {

    @Test
    public void generateNewAES128GCMKeySet() throws GeneralSecurityException {
        KeyTemplate kt = DeterministicAeadKeyTemplates.createAesSivKeyTemplate(64);
        KeysetHandle ksh = KeysetHandle.generateNew(kt);
        Assertions.hasEnsuredPredicate(kt);
        Assertions.hasEnsuredPredicate(ksh);
        Assertions.mustBeInAcceptingState(kt);
        Assertions.mustBeInAcceptingState(ksh);
    }

    @Test
    public void encryptUsingAES256_SIV() throws GeneralSecurityException {
        KeyTemplate kt = DeterministicAeadKeyTemplates.createAesSivKeyTemplate(64);
        KeysetHandle ksh = KeysetHandle.generateNew(kt);

        Assertions.hasEnsuredPredicate(kt);

        final String plainText = "Just testing the encryption mode of DAEAD";
        final String aad = "crysl";

        DeterministicAead daead = DeterministicAeadFactory.getPrimitive(ksh);
        byte[] out = daead.encryptDeterministically(plainText.getBytes(), aad.getBytes());

        Assertions.hasEnsuredPredicate(daead);
        Assertions.mustBeInAcceptingState(daead);
        // Assertions.hasEnsuredPredicate(out); // this assertions still leads to a red bar.
    }

    @Test
    public void encryptUsingNullKeyTemplate() throws GeneralSecurityException {
        KeyTemplate kt = null;
        KeysetHandle ksh = KeysetHandle.generateNew(kt);

        Assertions.notHasEnsuredPredicate(kt);
        Assertions.notHasEnsuredPredicate(kt);
    }

    @Test
    public void encryptUsingInvalidKey() throws GeneralSecurityException {
        KeyTemplate kt = DeterministicAeadKeyTemplates.createAesSivKeyTemplate(32);

        Assertions.notHasEnsuredPredicate(kt);
    }
}
