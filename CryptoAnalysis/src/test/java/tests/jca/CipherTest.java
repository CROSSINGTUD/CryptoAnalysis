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
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.Ruleset;
import test.TestRules;
import test.TestRunnerInterceptor;
import test.assertions.Assertions;

@ExtendWith(TestRunnerInterceptor.class)
@Ruleset(TestRules.JCA)
public class CipherTest {

    @Test
    public void noInit() throws GeneralSecurityException {
        Cipher c = Cipher.getInstance("trololo");

        Assertions.extValue(0);
        Assertions.mustNotBeInAcceptingState(c);
        Assertions.notHasEnsuredPredicate(c);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void yesInit() throws GeneralSecurityException {
        Cipher c = Cipher.getInstance("trololo");
        c.init(1, new SecretKeySpec(null, "trololo"));
        Assertions.extValue(0);
        Assertions.mustNotBeInAcceptingState(c);
        Assertions.notHasEnsuredPredicate(c);
    }

    @Test
    public void useDoFinalInLoop() throws GeneralSecurityException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        keygen.init(128);
        Assertions.extValue(0);
        SecretKey key = keygen.generateKey();
        Assertions.hasEnsuredPredicate(key);
        Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Assertions.extValue(0);
        cCipher.init(Cipher.ENCRYPT_MODE, key);
        Assertions.mustNotBeInAcceptingState(cCipher);
        byte[] enc = null;
        for (int i = 0; i < 42; i++) {
            enc = cCipher.doFinal("".getBytes());
            Assertions.mustBeInAcceptingState(cCipher);
            Assertions.notHasEnsuredPredicate(enc);
        }
        // Assertions.mustNotBeInAcceptingState(cCipher);
        Assertions.notHasEnsuredPredicate(enc);
    }

    @Test
    public void caseInsensitiveNames() throws GeneralSecurityException {
        KeyGenerator keygen = KeyGenerator.getInstance("aes");
        Assertions.extValue(0);
        keygen.init(128);
        Assertions.extValue(0);
        SecretKey key = keygen.generateKey();
        Assertions.hasEnsuredPredicate(key);
        Cipher cCipher = Cipher.getInstance("Aes/CbC/pKCS5PADDING");
        Assertions.extValue(0);
        cCipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] enc = cCipher.doFinal("".getBytes());
        Assertions.mustBeInAcceptingState(cCipher);
        Assertions.notHasEnsuredPredicate(enc);
    }

    @Test
    public void cipherUsagePatternTest1() throws GeneralSecurityException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        keygen.init(128);
        Assertions.extValue(0);
        SecretKey key = keygen.generateKey();
        Assertions.hasEnsuredPredicate(key);
        Assertions.mustBeInAcceptingState(keygen);
        String string = "AES/CBC/PKCS5Padding";
        Cipher cCipher = Cipher.getInstance(string);
        Assertions.extValue(0);
        cCipher.init(Cipher.ENCRYPT_MODE, key);

        Assertions.extValue(0);
        byte[] encText = cCipher.doFinal("".getBytes());
        Assertions.notHasEnsuredPredicate(encText);
        Assertions.mustBeInAcceptingState(cCipher);
    }

    @Test
    public void cipherUsagePatternImprecise() throws GeneralSecurityException {
        SecretKey key = KeyGenerator.getInstance("AES").generateKey();
        Assertions.hasEnsuredPredicate(key);

        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Assertions.extValue(0);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] res = c.doFinal("message".getBytes(), 0, "message".getBytes().length);
        Assertions.mustBeInAcceptingState(c);
        Assertions.notHasEnsuredPredicate(res);
    }

    @Test
    public void cipherUsagePatternTestInsecureKey() throws GeneralSecurityException {
        byte[] plaintext = "WHAT!?".getBytes();

        SecretKeySpec encKey = new SecretKeySpec(new byte[1], "AES");
        Assertions.notHasEnsuredPredicate(encKey);

        Cipher c = Cipher.getInstance("AES/CBC");
        c.init(1, encKey);
        String ciphertext = new String(c.doFinal(plaintext));
        Assertions.mustBeInAcceptingState(c);
        Assertions.notHasEnsuredPredicate(ciphertext);
    }

    @Test
    public void cipherUsagePatternTestInter1() throws GeneralSecurityException {
        SecretKey key = generateKey();
        encrypt(key);
    }

    @Test
    public void cipherUsagePatternTestInter2() throws GeneralSecurityException {
        SecretKey key = generateKey();
        forward(key);
    }

    private void forward(SecretKey key) throws GeneralSecurityException {
        SecretKey tmpKey = key;
        encrypt(tmpKey);
    }

    @Test
    public void cipherUsagePatternTestInter3() throws GeneralSecurityException {
        SecretKey key = generateKey();
        rebuild(key);
    }

    private void rebuild(SecretKey key) throws GeneralSecurityException {
        SecretKey tmpKey = new SecretKeySpec(key.getEncoded(), "AES");
        encrypt(tmpKey);
    }

    @Test
    public void cipherUsagePatternTestInter4() throws GeneralSecurityException {
        SecretKey key = generateKey();
        wrongRebuild(key);
    }

    private void wrongRebuild(SecretKey key) throws GeneralSecurityException {
        SecretKey tmpKey = new SecretKeySpec(key.getEncoded(), "DES");
        Assertions.notHasEnsuredPredicate(tmpKey);
        encryptWrong(tmpKey);
    }

    private void encryptWrong(SecretKey key) throws GeneralSecurityException {
        Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Assertions.extValue(0);
        cCipher.init(Cipher.ENCRYPT_MODE, key);

        Assertions.extValue(0);
        byte[] encText = cCipher.doFinal("".getBytes());
        Assertions.notHasEnsuredPredicate(encText);
        Assertions.mustBeInAcceptingState(cCipher);
    }

    private void encrypt(SecretKey key) throws GeneralSecurityException {
        Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Assertions.extValue(0);
        cCipher.init(Cipher.ENCRYPT_MODE, key);

        Assertions.extValue(0);
        byte[] encText = cCipher.doFinal("".getBytes());
        Assertions.notHasEnsuredPredicate(encText);
        Assertions.mustBeInAcceptingState(cCipher);
    }

    private SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        keygen.init(128);
        Assertions.extValue(0);
        SecretKey key = keygen.generateKey();
        Assertions.hasEnsuredPredicate(key);

        Assertions.mustBeInAcceptingState(keygen);
        return key;
    }

    @Test
    public void cipherUsagePatternTest1SilentForbiddenMethod() throws GeneralSecurityException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        keygen.init(128);
        Assertions.extValue(0);
        SecretKey key = keygen.generateKey();
        Assertions.hasEnsuredPredicate(key);
        Assertions.mustBeInAcceptingState(keygen);
        Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Assertions.extValue(0);
        cCipher.init(Cipher.DECRYPT_MODE, key);
        Assertions.extValue(0);
        // Assertions.callToForbiddenMethod();

        byte[] encText = cCipher.doFinal("".getBytes());
        Assertions.mustBeInAcceptingState(cCipher);
        Assertions.notHasEnsuredPredicate(cCipher);
        Assertions.notHasEnsuredPredicate(encText);
    }

    @Test
    public void cipherUsagePatternTest1a() throws GeneralSecurityException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        keygen.init(128);
        Assertions.extValue(0);
        SecretKey key = keygen.generateKey();
        Assertions.hasEnsuredPredicate(key);
        Assertions.mustBeInAcceptingState(keygen);

        byte[] iv = new byte[32];
        SecureRandom.getInstanceStrong().nextBytes(iv);
        GCMParameterSpec spec = new GCMParameterSpec(96, iv);

        Cipher cCipher = Cipher.getInstance("AES/GCM/NoPadding");
        Assertions.extValue(0);
        int mode = 1;
        if (Math.random() % 2 == 0) {
            mode = 2;
        }
        cCipher.init(mode, key, spec);
        Assertions.extValue(0);

        byte[] encText = cCipher.doFinal("".getBytes());
        Assertions.hasEnsuredPredicate(encText);
        Assertions.mustBeInAcceptingState(cCipher);
    }

    @Test
    public void cipherUsagePatternTestIVCor() throws GeneralSecurityException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        keygen.init(128);
        Assertions.extValue(0);
        SecretKey key = keygen.generateKey();

        Assertions.hasEnsuredPredicate(key);
        Assertions.mustBeInAcceptingState(keygen);

        SecureRandom sr = SecureRandom.getInstanceStrong();
        Assertions.hasEnsuredPredicate(sr);

        byte[] ivbytes = new byte[12];
        sr.nextBytes(ivbytes);
        Assertions.hasEnsuredPredicate(ivbytes);

        IvParameterSpec iv = new IvParameterSpec(ivbytes);
        Assertions.mustBeInAcceptingState(iv);
        Assertions.hasEnsuredPredicate(iv);

        Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Assertions.extValue(0);
        cCipher.init(Cipher.ENCRYPT_MODE, key, iv);

        Assertions.extValue(0);
        byte[] encText = cCipher.doFinal("".getBytes());
        Assertions.notHasEnsuredPredicate(encText);
        Assertions.mustBeInAcceptingState(cCipher);
    }

    @Test
    public void cipherUsagePatternTestIVInCor() throws GeneralSecurityException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        keygen.init(128);
        Assertions.extValue(0);
        SecretKey key = keygen.generateKey();

        Assertions.hasEnsuredPredicate(key);
        Assertions.mustBeInAcceptingState(keygen);

        byte[] ivbytes = new byte[12];
        Assertions.notHasEnsuredPredicate(ivbytes);

        IvParameterSpec iv = new IvParameterSpec(ivbytes);
        Assertions.mustBeInAcceptingState(iv);
        Assertions.notHasEnsuredPredicate(iv);

        Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Assertions.extValue(0);
        cCipher.init(Cipher.ENCRYPT_MODE, key, iv);

        Assertions.extValue(0);
        byte[] encText = cCipher.doFinal("".getBytes());
        Assertions.notHasEnsuredPredicate(encText);
        Assertions.mustBeInAcceptingState(cCipher);
    }

    @Test
    public void cipherUsagePatternTestWrongOffsetSize() throws GeneralSecurityException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        keygen.init(128);
        Assertions.extValue(0);
        SecretKey key = keygen.generateKey();
        Assertions.hasEnsuredPredicate(key);
        Assertions.mustBeInAcceptingState(keygen);

        Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Assertions.extValue(0);
        cCipher.init(Cipher.ENCRYPT_MODE, key);
        Assertions.extValue(0);
        final byte[] bytes = "test".getBytes();
        byte[] encText = cCipher.doFinal(bytes, 200, bytes.length);
        Assertions.extValue(0);
        Assertions.extValue(1);
        Assertions.extValue(2);
        Assertions.mustBeInAcceptingState(cCipher);
        Assertions.notHasEnsuredPredicate(encText);
    }

    @Test
    public void cipherUsagePatternTestMissingMode() throws GeneralSecurityException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        keygen.init(128);
        Assertions.extValue(0);
        SecretKey key = keygen.generateKey();
        Assertions.hasEnsuredPredicate(key);
        Assertions.mustBeInAcceptingState(keygen);
        Cipher cCipher = Cipher.getInstance("AES");
        Assertions.extValue(0);
        cCipher.init(Cipher.ENCRYPT_MODE, key);

        Assertions.extValue(0);
        byte[] encText = cCipher.doFinal("".getBytes());
        Assertions.notHasEnsuredPredicate(encText);
        Assertions.mustBeInAcceptingState(cCipher);
    }

    @Test
    public void cipherUsagePatternTestWrongPadding() throws GeneralSecurityException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        keygen.init(128);
        Assertions.extValue(0);
        SecretKey key = keygen.generateKey();
        Assertions.hasEnsuredPredicate(key);
        Assertions.mustBeInAcceptingState(keygen);
        Cipher cCipher = Cipher.getInstance("AES/CBC/NoPadding");
        Assertions.extValue(0);
        cCipher.init(Cipher.ENCRYPT_MODE, key);

        Assertions.extValue(0);
        byte[] encText = cCipher.doFinal("".getBytes());
        Assertions.notHasEnsuredPredicate(encText);
        Assertions.mustBeInAcceptingState(cCipher);
    }

    @Test
    public void cipherUsagePatternTest2() throws GeneralSecurityException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        keygen.init(129);
        Assertions.extValue(0);
        SecretKey key = keygen.generateKey();
        Assertions.mustBeInAcceptingState(keygen);
        Assertions.notHasEnsuredPredicate(key);

        Cipher cCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        Assertions.extValue(0);
        cCipher.init(Cipher.ENCRYPT_MODE, key);
        Assertions.extValue(0);
        byte[] encText = cCipher.doFinal("".getBytes());
        Assertions.notHasEnsuredPredicate(encText);
    }

    @Test
    public void cipherUsagePatternTest3() throws GeneralSecurityException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        keygen.init(128);
        Assertions.extValue(0);
        SecretKey key = keygen.generateKey();
        Assertions.mustBeInAcceptingState(keygen);
        Assertions.hasEnsuredPredicate(key);
        Cipher cCipher = Cipher.getInstance("AES");
        cCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(new byte[18], "AES"));
        Assertions.extValue(0);
        byte[] encText = cCipher.doFinal("".getBytes());
        Assertions.mustBeInAcceptingState(cCipher);
        Assertions.notHasEnsuredPredicate(encText);
    }

    @Test
    public void cipherUsagePatternTestWrongModeExtraVar() throws GeneralSecurityException {
        String trans = "AES";
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        keygen.init(128);
        Assertions.extValue(0);
        SecretKey key = keygen.generateKey();
        Assertions.mustBeInAcceptingState(keygen);
        Cipher cCipher = Cipher.getInstance(trans);
        Assertions.extValue(0);
        cCipher.init(Cipher.ENCRYPT_MODE, key);
        Assertions.extValue(0);
        byte[] encText = cCipher.doFinal("".getBytes());
        Assertions.mustBeInAcceptingState(cCipher);
        Assertions.notHasEnsuredPredicate(encText);
    }

    @Test
    public void cipherUsagePatternTest4() throws GeneralSecurityException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        keygen.init(128);
        Assertions.extValue(0);
        SecretKey key = keygen.generateKey();
        Assertions.mustBeInAcceptingState(keygen);

        Cipher cCipher = Cipher.getInstance("Blowfish");
        Assertions.extValue(0);
        cCipher.init(Cipher.ENCRYPT_MODE, key);
        Assertions.extValue(0);
        byte[] encText = cCipher.doFinal("".getBytes());
        Assertions.mustBeInAcceptingState(cCipher);
        Assertions.notHasEnsuredPredicate(encText);
    }

    @Test
    public void cipherUsagePatternTest5() throws GeneralSecurityException {
        final byte[] msgAsArray = "Message".getBytes();

        KeyGenerator keygenEnc = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        keygenEnc.init(128);
        Assertions.extValue(0);
        SecretKey keyEnc = keygenEnc.generateKey();
        Assertions.mustBeInAcceptingState(keygenEnc);

        Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Assertions.extValue(0);
        cCipher.init(Cipher.ENCRYPT_MODE, keyEnc);
        Assertions.extValue(0);
        byte[] encText = cCipher.doFinal(msgAsArray);
        Assertions.mustBeInAcceptingState(cCipher);
        Assertions.notHasEnsuredPredicate(encText);

        KeyGenerator keygenMac = KeyGenerator.getInstance("HmacSHA256");
        SecretKey keyMac = keygenMac.generateKey();

        final Mac hMacSHA256 = Mac.getInstance("HmacSHA256");
        Assertions.extValue(0);
        hMacSHA256.init(keyMac);
        byte[] macced = hMacSHA256.doFinal(msgAsArray);
        Assertions.mustBeInAcceptingState(hMacSHA256);
        Assertions.hasEnsuredPredicate(macced);
    }

    @Test
    public void cipherUsagePatternTest6() throws GeneralSecurityException {
        SecureRandom keyRand = SecureRandom.getInstanceStrong();
        Assertions.hasEnsuredPredicate(keyRand);

        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        keygen.init(128, keyRand);
        Assertions.extValue(0);
        SecretKey key = keygen.generateKey();
        Assertions.mustBeInAcceptingState(keygen);

        SecureRandom encRand = SecureRandom.getInstanceStrong();

        Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Assertions.extValue(0);
        cCipher.init(Cipher.ENCRYPT_MODE, key, encRand);
        Assertions.extValue(0);
        byte[] encText = cCipher.doFinal("".getBytes());
        Assertions.mustBeInAcceptingState(cCipher);
        Assertions.notHasEnsuredPredicate(encText);
    }

    @Test
    public void cipherUsagePatternTest7() throws GeneralSecurityException {
        SecureRandom rand = SecureRandom.getInstanceStrong();

        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        keygen.init(128);
        Assertions.extValue(0);
        SecretKey key = keygen.generateKey();
        Assertions.mustBeInAcceptingState(keygen);

        Cipher cCipher = Cipher.getInstance("Blowfish");
        Assertions.extValue(0);
        cCipher.init(Cipher.ENCRYPT_MODE, key, rand);
        Assertions.extValue(0);
        byte[] encText = cCipher.doFinal("".getBytes());
        Assertions.mustBeInAcceptingState(cCipher);
        Assertions.notHasEnsuredPredicate(encText);
    }

    @Test
    public void cipherUsagePatternTest7b() throws GeneralSecurityException {
        SecureRandom encRand = SecureRandom.getInstanceStrong();

        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        keygen.init(128, null);
        Assertions.extValue(0);
        SecretKey key = keygen.generateKey();
        Assertions.mustBeInAcceptingState(keygen);
        Assertions.notHasEnsuredPredicate(key);

        Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Assertions.extValue(0);
        cCipher.init(Cipher.ENCRYPT_MODE, key, encRand);
        Assertions.extValue(0);
        byte[] encText = cCipher.doFinal("".getBytes());
        Assertions.mustBeInAcceptingState(cCipher);
        Assertions.notHasEnsuredPredicate(encText);
    }

    @Test
    public void cipherUsagePatternTest8() throws GeneralSecurityException {
        String aesString = "AES";
        KeyGenerator keygen = KeyGenerator.getInstance(aesString);
        Assertions.extValue(0);
        int keySize = 128;
        int a = keySize;
        keygen.init(a);
        Assertions.extValue(0);
        SecretKey key = keygen.generateKey();
        Assertions.hasEnsuredPredicate(key);
        Assertions.mustBeInAcceptingState(keygen);
        Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Assertions.extValue(0);
        cCipher.init(Cipher.ENCRYPT_MODE, key);
        Assertions.extValue(0);
        byte[] encText = cCipher.doFinal("".getBytes());
        Assertions.notHasEnsuredPredicate(encText);
        Assertions.mustBeInAcceptingState(cCipher);
    }

    @Test
    public void cipherUsagePatternTest9() throws GeneralSecurityException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        Assertions.extValue(0);
        keygen.init(1);
        Assertions.extValue(0);
        SecretKey key = keygen.generateKey();
        Assertions.notHasEnsuredPredicate(key);
        Assertions.mustBeInAcceptingState(keygen);
        Cipher cCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Assertions.extValue(0);
        cCipher.init(Cipher.ENCRYPT_MODE, key);

        Assertions.extValue(0);
        byte[] encText = cCipher.doFinal("".getBytes());
        Assertions.notHasEnsuredPredicate(encText);
        Assertions.mustBeInAcceptingState(cCipher);
    }
}
