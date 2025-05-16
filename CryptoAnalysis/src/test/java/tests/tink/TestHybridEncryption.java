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

import com.google.crypto.tink.HybridDecrypt;
import com.google.crypto.tink.HybridEncrypt;
import com.google.crypto.tink.KeyTemplate;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.hybrid.EciesAeadHkdfPrivateKeyManager;
import com.google.crypto.tink.hybrid.HybridDecryptFactory;
import com.google.crypto.tink.hybrid.HybridEncryptFactory;
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
public class TestHybridEncryption {

    @Test
    public void generateNewECIES_P256_HKDF_HMAC_SHA256_AES128_GCMKeySet()
            throws GeneralSecurityException {

        KeyTemplate kt = EciesAeadHkdfPrivateKeyManager.eciesP256HkdfHmacSha256Aes128GcmTemplate();

        KeysetHandle ksh = KeysetHandle.generateNew(kt);
        Assertions.hasEnsuredPredicate(kt);
        Assertions.mustBeInAcceptingState(kt);
        Assertions.mustBeInAcceptingState(ksh);
    }

    @Test
    public void generateNewECIES_P256_HKDF_HMAC_SHA256_AES128_CTR_HMAC_SHA256KeySet()
            throws GeneralSecurityException {

        KeyTemplate kt =
                EciesAeadHkdfPrivateKeyManager.eciesP256HkdfHmacSha256Aes128CtrHmacSha256Template();
        KeysetHandle ksh = KeysetHandle.generateNew(kt);
        Assertions.hasEnsuredPredicate(kt);
        Assertions.mustBeInAcceptingState(kt);
        Assertions.mustBeInAcceptingState(ksh);
    }

    @Test
    public void generateInvalidKey() {
        KeyTemplate kt = null;
        Assertions.notHasEnsuredPredicate(kt);
    }

    @Test
    public void encryptUsingECIES_P256_HKDF_HMAC_SHA256_AES128_CTR_HMAC_SHA256KeySet()
            throws GeneralSecurityException {
        KeyTemplate kt =
                EciesAeadHkdfPrivateKeyManager.eciesP256HkdfHmacSha256Aes128CtrHmacSha256Template();

        KeysetHandle ksh = KeysetHandle.generateNew(kt);
        KeysetHandle publicKsh = ksh.getPublicKeysetHandle();

        HybridEncrypt cipher = HybridEncryptFactory.getPrimitive(publicKsh);

        byte[] cipherText =
                cipher.encrypt("just an hybrid encryption test".getBytes(), "".getBytes());

        Assertions.hasEnsuredPredicate(kt);
        Assertions.hasEnsuredPredicate(publicKsh);
        Assertions.hasEnsuredPredicate(cipher);
        Assertions.mustBeInAcceptingState(kt);
        Assertions.mustBeInAcceptingState(ksh);
        Assertions.mustBeInAcceptingState(publicKsh);
    }

    @Test
    public void decryptUsingECIES_P256_HKDF_HMAC_SHA256_AES128_CTR_HMAC_SHA256KeySet()
            throws GeneralSecurityException {
        KeyTemplate kt =
                EciesAeadHkdfPrivateKeyManager.eciesP256HkdfHmacSha256Aes128CtrHmacSha256Template();
        KeysetHandle ksh = KeysetHandle.generateNew(kt);

        HybridDecrypt cipher = HybridDecryptFactory.getPrimitive(ksh);

        byte[] cipherText =
                cipher.decrypt("mxvw d sodlq whaw iru whvwlqj".getBytes(), "".getBytes());

        Assertions.hasEnsuredPredicate(kt);
        Assertions.hasEnsuredPredicate(ksh);
        Assertions.hasEnsuredPredicate(cipher);
        Assertions.mustBeInAcceptingState(kt);
        Assertions.mustBeInAcceptingState(ksh);
    }
}
