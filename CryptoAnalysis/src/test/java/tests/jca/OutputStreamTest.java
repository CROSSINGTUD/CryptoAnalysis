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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestOutputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.junit.Ignore;
import org.junit.Test;
import test.TestConstants;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class OutputStreamTest extends UsagePatternTestingFramework {

    @Override
    protected String getRulesetPath() {
        return TestConstants.JCA_RULESET_PATH;
    }

    // Usage Pattern for CipherOutputStream
    @Test
    public void UsagePatternTestCOSDefaultUse() throws GeneralSecurityException, IOException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        keyGenerator.init(128);
        Assertions.extValue(0);
        SecretKey key = keyGenerator.generateKey();
        Assertions.hasEnsuredPredicate(key);
        Assertions.mustBeInAcceptingState(keyGenerator);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Assertions.extValue(0);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        Assertions.extValue(0);

        OutputStream os = Files.newOutputStream(Paths.get(".\\resources\\cos.txt"));
        CipherOutputStream cos = new CipherOutputStream(os, cipher);
        cos.write("Hello World\n".getBytes());
        cos.close();
        Assertions.mustBeInAcceptingState(cos);
    }

    @Test
    public void UsagePatternTestCOSAdditionalUse() throws GeneralSecurityException, IOException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        keyGenerator.init(128);
        Assertions.extValue(0);
        SecretKey key = keyGenerator.generateKey();
        Assertions.hasEnsuredPredicate(key);
        Assertions.mustBeInAcceptingState(keyGenerator);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Assertions.extValue(0);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        Assertions.extValue(0);

        OutputStream os = Files.newOutputStream(Paths.get(".\\resources\\cos.txt"));
        CipherOutputStream cos = new CipherOutputStream(os, cipher);
        cos.write("message".getBytes(), 0, "message".getBytes().length);
        cos.close();
        Assertions.mustBeInAcceptingState(cos);
    }

    @Test
    public void UsagePatternTestCOSMissingCallToClose()
            throws GeneralSecurityException, IOException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        keyGenerator.init(128);
        Assertions.extValue(0);
        SecretKey key = keyGenerator.generateKey();
        Assertions.hasEnsuredPredicate(key);
        Assertions.mustBeInAcceptingState(keyGenerator);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Assertions.extValue(0);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        Assertions.extValue(0);

        OutputStream os = Files.newOutputStream(Paths.get(".\\resources\\cos.txt"));
        CipherOutputStream cos = new CipherOutputStream(os, cipher);
        cos.write("Hello World\n".getBytes());
        Assertions.mustNotBeInAcceptingState(cos);
        cos.close();
    }

    @Test
    public void UsagePatternTestCOSViolatedConstraint()
            throws GeneralSecurityException, IOException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        keyGenerator.init(128);
        Assertions.extValue(0);
        SecretKey key = keyGenerator.generateKey();
        Assertions.hasEnsuredPredicate(key);
        Assertions.mustBeInAcceptingState(keyGenerator);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Assertions.extValue(0);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        Assertions.extValue(0);

        OutputStream os = Files.newOutputStream(Paths.get(".\\resources\\cos.txt"));
        CipherOutputStream cos = new CipherOutputStream(os, cipher);
        cos.write("message".getBytes(), 100, "message".getBytes().length);
        // Assertions.violatedConstraint();
        Assertions.mustNotBeInAcceptingState(cos);
        cos.close();
    }

    // Usage Pattern tests for DigestOutputStream
    @Test
    @Ignore
    public void UsagePatternTestDOSCallToForbiddenMethod()
            throws GeneralSecurityException, IOException {
        OutputStream os = Files.newOutputStream(Paths.get(".\\resources\\dos.txt"));
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        Assertions.extValue(0);
        DigestOutputStream dos = new DigestOutputStream(os, md);
        dos.on(false);
        Assertions.callToForbiddenMethod();
        dos.write("Hello World\n".getBytes());
        Assertions.mustBeInAcceptingState(dos);
    }

    @Test
    public void UsagePatternTestDOSMissingCallToWrite()
            throws GeneralSecurityException, IOException {
        OutputStream os = Files.newOutputStream(Paths.get(".\\resources\\dos.txt"));
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        Assertions.extValue(0);
        DigestOutputStream dos = new DigestOutputStream(os, md);
        Assertions.mustNotBeInAcceptingState(dos);
        dos.write("Hello World".getBytes());
    }

    @Test
    public void UsagePatternTestDOSAdditionalUse() throws GeneralSecurityException, IOException {
        OutputStream os = Files.newOutputStream(Paths.get(".\\resources\\dos.txt"));
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        Assertions.extValue(0);
        DigestOutputStream dos = new DigestOutputStream(os, md);
        dos.write("message".getBytes(), 0, "message".getBytes().length);
        dos.close();
        Assertions.mustBeInAcceptingState(dos);
    }

    @Test
    public void UsagePatternTestDOSViolatedConstraint()
            throws GeneralSecurityException, IOException {
        OutputStream os = Files.newOutputStream(Paths.get(".\\resources\\dos.txt"));
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        Assertions.extValue(0);
        DigestOutputStream dos = new DigestOutputStream(os, md);
        dos.write("message".getBytes(), 100, "message".getBytes().length);
        // Assertions.violatedConstraint();
        Assertions.mustNotBeInAcceptingState(dos);
    }
}
