/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package tests.endoflifecycle;

import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.security.auth.DestroyFailedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.Ruleset;
import test.TestRules;
import test.TestRunnerInterceptor;
import test.assertions.Assertions;

@ExtendWith(TestRunnerInterceptor.class)
@Ruleset(TestRules.JCA)
public class EndOfLifeCycleErrorTest {

    @Test
    public void missingDoFinalCall() throws GeneralSecurityException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(128);
        SecretKey key = keygen.generateKey();
        Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cCipher.init(Cipher.ENCRYPT_MODE, key);
        Assertions.missingTypestateChange();
    }

    @Test
    public void missingGenerateKey() throws GeneralSecurityException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(128);
        Assertions.missingTypestateChange();
    }

    @Test
    public void missingGenerateKeyCaught() throws GeneralSecurityException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(128);
        Assertions.missingTypestateChange();
    }

    @Test
    public void missingDoFinalCall2() throws GeneralSecurityException, DestroyFailedException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(128);
        SecretKey key = keygen.generateKey();
        Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cCipher.init(Cipher.ENCRYPT_MODE, key);
        Assertions.missingTypestateChange();
        key.destroy();
    }

    @Test
    public void missingDoFinalCall3() throws GeneralSecurityException, DestroyFailedException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(128);
        SecretKey key = keygen.generateKey();
        Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Container con = new Container();
        con.c = cCipher;
        cCipher.init(Cipher.ENCRYPT_MODE, key);
        Cipher cipher = con.c;
        cipher.getAlgorithm();
        Assertions.missingTypestateChange();
        key.destroy();
    }

    @Test
    public void missingDoFinalCall5() throws GeneralSecurityException, DestroyFailedException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(128);
        SecretKey key = keygen.generateKey();
        Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Container con = new Container();
        con.c = cCipher;
        cCipher.init(Cipher.ENCRYPT_MODE, key);
        Cipher cipher = con.c;
        cipher.doFinal(null);
        Assertions.noMissingTypestateChange();
        cipher.getAlgorithm();
        key.destroy();
    }

    private static class Container {
        Cipher c;
    }
}
