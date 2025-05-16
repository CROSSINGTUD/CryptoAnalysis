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

import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.Mac;
import com.google.crypto.tink.mac.MacFactory;
import com.google.crypto.tink.mac.MacKeyTemplates;
import com.google.crypto.tink.proto.HashType;
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
public class TestMAC {

    @Test
    public void generateNewHMACSHA256_128BitTag() throws GeneralSecurityException {
        KeyTemplate kt = MacKeyTemplates.createHmacKeyTemplate(32, 16, HashType.SHA256);
        KeysetHandle ksh = KeysetHandle.generateNew(kt);
        Assertions.mustBeInAcceptingState(kt);
        Assertions.mustBeInAcceptingState(ksh);
    }

    @Test
    public void generateNewHMACSHA256_256BitTag() throws GeneralSecurityException {
        KeyTemplate kt = MacKeyTemplates.createHmacKeyTemplate(32, 32, HashType.SHA256);
        KeysetHandle ksh = KeysetHandle.generateNew(kt);
        Assertions.mustBeInAcceptingState(kt);
        Assertions.mustBeInAcceptingState(ksh);
    }

    @Test
    public void testGenerateMAC() throws GeneralSecurityException {
        KeyTemplate kt = MacKeyTemplates.createHmacKeyTemplate(32, 16, HashType.SHA256);
        KeysetHandle ksh = KeysetHandle.generateNew(kt);

        Mac mac = MacFactory.getPrimitive(ksh);

        final byte[] data = "This is just a sample text".getBytes();
        final byte[] tag = mac.computeMac(data);

        mac.verifyMac(tag, data);

        Assertions.mustBeInAcceptingState(mac);
    }
}
