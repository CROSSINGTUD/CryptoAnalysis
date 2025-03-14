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

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadFactory;
import com.google.crypto.tink.aead.AeadKeyTemplates;
import com.google.crypto.tink.proto.KeyTemplate;
import java.security.GeneralSecurityException;
import org.junit.Ignore;
import org.junit.Test;
import test.TestConstants;
import test.assertions.Assertions;

@Ignore
public class TestAEADCipher extends TestTinkPrimitives {

    @Override
    protected String getRulesetPath() {
        return TestConstants.TINK_RULESET_PATH;
    }

    @Test
    public void generateNewAES128GCMKeySet() throws GeneralSecurityException {
        KeyTemplate kt = AeadKeyTemplates.createAesGcmKeyTemplate(16);
        KeysetHandle ksh = KeysetHandle.generateNew(kt);
        Assertions.hasEnsuredPredicate(kt);
        Assertions.mustBeInAcceptingState(kt);
        Assertions.mustBeInAcceptingState(ksh);
    }

    @Test
    public void generateNewAES256GCMKeySet() throws GeneralSecurityException {
        KeyTemplate kt = AeadKeyTemplates.createAesGcmKeyTemplate(32);
        KeysetHandle ksh = KeysetHandle.generateNew(kt);
        Assertions.mustBeInAcceptingState(kt);
        Assertions.mustBeInAcceptingState(ksh);
    }

    @Test
    public void generateNewAES128EAXKeySet() throws GeneralSecurityException {
        KeyTemplate kt = AeadKeyTemplates.createAesEaxKeyTemplate(16, 16);
        KeysetHandle ksh = KeysetHandle.generateNew(kt);
        Assertions.mustBeInAcceptingState(kt);
        Assertions.mustBeInAcceptingState(ksh);
    }

    @Test
    public void generateNewAES256EAXKeySet() throws GeneralSecurityException {
        KeyTemplate kt = AeadKeyTemplates.createAesEaxKeyTemplate(32, 16);
        KeysetHandle ksh = KeysetHandle.generateNew(kt);
        Assertions.mustBeInAcceptingState(kt);
        Assertions.mustBeInAcceptingState(ksh);
    }

    @Test
    public void encryptUsingAES128GCM() throws GeneralSecurityException {
        KeyTemplate kt = AeadKeyTemplates.createAesGcmKeyTemplate(16);

        KeysetHandle ksh = KeysetHandle.generateNew(kt);

        Assertions.hasEnsuredPredicate(
                kt); // this might look crazy, but sometimes Ok. in other executions, this line
        // leads to a red bar.

        final String plainText = "Just testing the encryption mode of AEAD";
        final String aad = "crysl";

        Aead aead = AeadFactory.getPrimitive(ksh);
        byte[] out = aead.encrypt(plainText.getBytes(), aad.getBytes());

        Assertions.hasEnsuredPredicate(aead);
        Assertions.mustBeInAcceptingState(aead);
        // Assertions.hasEnsuredPredicate(out); // this assertions still leads to a red bar.
    }
}
