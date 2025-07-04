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
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStore.Entry;
import java.security.KeyStore.LoadStoreParameter;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertSelector;
import java.security.cert.CertificateException;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.TrustAnchor;
import java.util.Set;
import javax.net.ssl.SSLParameters;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.Ruleset;
import test.TestRules;
import test.TestRunnerInterceptor;
import test.assertions.Assertions;

@ExtendWith(TestRunnerInterceptor.class)
@Ruleset(TestRules.JCA)
public class CogniCryptTestGenTest {

    @Test
    @SuppressWarnings("ConstantConditions")
    public void pKIXBuilderParametersValidTest2() throws InvalidAlgorithmParameterException {
        // Related to issue 296: https://github.com/CROSSINGTUD/CryptoAnalysis/issues/296
        Set<TrustAnchor> trustAnchors = null;
        CertSelector certSelector = null;

        PKIXBuilderParameters pKIXBuilderParameters0 =
                new PKIXBuilderParameters(trustAnchors, certSelector);
        Assertions.hasEnsuredPredicate(pKIXBuilderParameters0);
        Assertions.mustBeInAcceptingState(pKIXBuilderParameters0);
    }

    @Disabled("Requires proper implementation of keyword 'elements'")
    @Test
    @SuppressWarnings("ConstantConditions")
    public void sSLParametersValidTest1() {
        // Related to issue 296: https://github.com/CROSSINGTUD/CryptoAnalysis/issues/296
        SSLParameters sSLParameters0 =
                new SSLParameters(
                        new String[] {"TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384"},
                        new String[] {"TLSv1.2"});
        Assertions.hasEnsuredPredicate(sSLParameters0);
        Assertions.mustBeInAcceptingState(sSLParameters0);
    }

    @Disabled("Requires proper implementation of keyword 'elements'")
    @Test
    public void sSLParametersValidTest2() {
        // Related to issue 296: https://github.com/CROSSINGTUD/CryptoAnalysis/issues/296
        SSLParameters sSLParameters0 =
                new SSLParameters(new String[] {"TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384"});
        sSLParameters0.setProtocols(new String[] {"TLSv1.2"});
        Assertions.hasEnsuredPredicate(sSLParameters0);
        Assertions.mustBeInAcceptingState(sSLParameters0);
    }

    @Disabled("Requires proper implementation of keyword 'elements'")
    @Test
    public void sSLParametersValidTest3() {
        // Related to issue 296: https://github.com/CROSSINGTUD/CryptoAnalysis/issues/296
        SSLParameters sSLParameters0 =
                new SSLParameters(new String[] {"TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384"});
        sSLParameters0.setProtocols(new String[] {"TLSv1.2"});
        Assertions.hasEnsuredPredicate(sSLParameters0);
        Assertions.mustBeInAcceptingState(sSLParameters0);
    }

    @Disabled("Requires proper implementation of keyword 'elements'")
    @Test
    public void sSLParametersInvalidTest2() {
        // Related to issue 296: https://github.com/CROSSINGTUD/CryptoAnalysis/issues/296
        SSLParameters sSLParameters0 =
                new SSLParameters(new String[] {"TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384"});
        Assertions.notHasEnsuredPredicate(sSLParameters0);
        Assertions.mustNotBeInAcceptingState(sSLParameters0);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void keyStoreInvalidTest7()
            throws NoSuchAlgorithmException,
                    IOException,
                    KeyStoreException,
                    CertificateException,
                    UnrecoverableEntryException {

        // Related to issue 295: https://github.com/CROSSINGTUD/CryptoAnalysis/issues/295
        char[] passwordKey = null;
        String aliasGet = null;
        String alias = null;
        Entry entry = null;
        String keyStoreAlgorithm = null;
        String aliasSet = null;
        ProtectionParameter protParamSet = null;
        LoadStoreParameter paramStore = null;
        ProtectionParameter protParamGet = null;

        KeyStore keyStore0 = KeyStore.getInstance(keyStoreAlgorithm);
        // loads skipped
        keyStore0.getEntry(aliasGet, protParamGet);
        Key key = keyStore0.getKey(alias, passwordKey);
        keyStore0.setEntry(aliasSet, entry, protParamSet);
        keyStore0.store(paramStore);
        Assertions.notHasEnsuredPredicate(key);
        Assertions.mustNotBeInAcceptingState(keyStore0);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void keyStoreInvalidTest8()
            throws NoSuchAlgorithmException,
                    IOException,
                    KeyStoreException,
                    CertificateException,
                    UnrecoverableEntryException {

        // Related to issue 295: https://github.com/CROSSINGTUD/CryptoAnalysis/issues/295
        char[] passwordKey = null;
        String aliasGet = null;
        String alias = null;
        Entry entry = null;
        String keyStoreAlgorithm = null;
        String aliasSet = null;
        ProtectionParameter protParamSet = null;
        LoadStoreParameter paramStore = null;
        ProtectionParameter protParamGet = null;

        KeyStore keyStore0 = KeyStore.getInstance(keyStoreAlgorithm, (Provider) null);
        // loads skipped
        keyStore0.getEntry(aliasGet, protParamGet);
        Key key = keyStore0.getKey(alias, passwordKey);
        keyStore0.setEntry(aliasSet, entry, protParamSet);
        keyStore0.store(paramStore);
        Assertions.notHasEnsuredPredicate(key);
        Assertions.mustNotBeInAcceptingState(keyStore0);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void keyStoreInvalidTest9()
            throws NoSuchAlgorithmException,
                    IOException,
                    KeyStoreException,
                    CertificateException,
                    UnrecoverableEntryException {

        // Related to issue 295: https://github.com/CROSSINGTUD/CryptoAnalysis/issues/295
        char[] passwordKey = null;
        String aliasGet = null;
        String alias = null;
        Entry entry = null;
        String keyStoreAlgorithm = null;
        String aliasSet = null;
        ProtectionParameter protParamSet = null;
        OutputStream fileoutput = null;
        char[] passwordOut = null;
        ProtectionParameter protParamGet = null;

        KeyStore keyStore0 = KeyStore.getInstance(keyStoreAlgorithm);
        // loads skipped
        keyStore0.getEntry(aliasGet, protParamGet);
        Key key = keyStore0.getKey(alias, passwordKey);
        keyStore0.setEntry(aliasSet, entry, protParamSet);
        keyStore0.store(fileoutput, passwordOut);
        Assertions.notHasEnsuredPredicate(key);
        Assertions.mustNotBeInAcceptingState(keyStore0);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void keyStoreInvalidTest10()
            throws NoSuchAlgorithmException,
                    UnrecoverableKeyException,
                    IOException,
                    KeyStoreException,
                    CertificateException {

        // Related to issue 296: https://github.com/CROSSINGTUD/CryptoAnalysis/issues/296
        char[] passwordKey = null;
        String alias = null;
        Entry entry = null;
        InputStream fileInput = null;
        String keyStoreAlgorithm = null;
        String aliasSet = null;
        ProtectionParameter protParamSet = null;
        char[] passwordIn = null;
        LoadStoreParameter paramStore = null;

        KeyStore keyStore0 = KeyStore.getInstance(keyStoreAlgorithm);
        keyStore0.load(fileInput, passwordIn);
        Key key = keyStore0.getKey(alias, passwordKey);
        Assertions.notHasEnsuredPredicate(key);
        keyStore0.setEntry(aliasSet, entry, protParamSet);
        keyStore0.store(paramStore);
        Assertions.mustBeInAcceptingState(keyStore0);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void keyStoreInvalidTest11()
            throws NoSuchAlgorithmException,
                    UnrecoverableKeyException,
                    IOException,
                    KeyStoreException,
                    CertificateException {

        // Related to issue 296: https://github.com/CROSSINGTUD/CryptoAnalysis/issues/296
        char[] passwordKey = null;
        String alias = null;
        Entry entry = null;
        InputStream fileInput = null;
        String keyStoreAlgorithm = null;
        String aliasSet = null;
        ProtectionParameter protParamSet = null;
        char[] passwordIn = null;
        LoadStoreParameter paramStore = null;

        KeyStore keyStore0 = KeyStore.getInstance(keyStoreAlgorithm, (Provider) null);
        keyStore0.load(fileInput, passwordIn);
        Key key = keyStore0.getKey(alias, passwordKey);
        Assertions.notHasEnsuredPredicate(key);
        keyStore0.setEntry(aliasSet, entry, protParamSet);
        keyStore0.store(paramStore);
        Assertions.mustBeInAcceptingState(keyStore0);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void keyStoreInvalidTest12()
            throws NoSuchAlgorithmException,
                    UnrecoverableKeyException,
                    IOException,
                    KeyStoreException,
                    CertificateException {

        // Related to issue 296: https://github.com/CROSSINGTUD/CryptoAnalysis/issues/296
        char[] passwordKey = null;
        String alias = null;
        Entry entry = null;
        LoadStoreParameter paramLoad = null;
        String keyStoreAlgorithm = null;
        String aliasSet = null;
        ProtectionParameter protParamSet = null;
        LoadStoreParameter paramStore = null;

        KeyStore keyStore0 = KeyStore.getInstance(keyStoreAlgorithm);
        keyStore0.load(paramLoad);
        Key key = keyStore0.getKey(alias, passwordKey);
        Assertions.notHasEnsuredPredicate(key);
        keyStore0.setEntry(aliasSet, entry, protParamSet);
        keyStore0.store(paramStore);
        Assertions.mustBeInAcceptingState(keyStore0);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void keyStoreInvalidTest13()
            throws NoSuchAlgorithmException,
                    UnrecoverableKeyException,
                    IOException,
                    KeyStoreException,
                    CertificateException {

        // Related to issue 296: https://github.com/CROSSINGTUD/CryptoAnalysis/issues/296
        char[] passwordKey = null;
        String alias = null;
        Entry entry = null;
        InputStream fileInput = null;
        String keyStoreAlgorithm = null;
        String aliasSet = null;
        ProtectionParameter protParamSet = null;
        OutputStream fileOutput = null;
        char[] passwordOut = null;
        char[] passwordIn = null;

        KeyStore keyStore0 = KeyStore.getInstance(keyStoreAlgorithm);
        keyStore0.load(fileInput, passwordIn);
        Key key = keyStore0.getKey(alias, passwordKey);
        Assertions.notHasEnsuredPredicate(key);
        keyStore0.setEntry(aliasSet, entry, protParamSet);
        keyStore0.store(fileOutput, passwordOut);
        Assertions.mustBeInAcceptingState(keyStore0);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void keyStoreInvalidTest14()
            throws NoSuchAlgorithmException,
                    IOException,
                    KeyStoreException,
                    CertificateException,
                    UnrecoverableEntryException {

        // Related to issue 296: https://github.com/CROSSINGTUD/CryptoAnalysis/issues/296
        String aliasGet = null;
        Entry entry = null;
        InputStream fileInput = null;
        String keyStoreAlgorithm = null;
        String aliasSet = null;
        ProtectionParameter protParamSet = null;
        char[] passwordIn = null;
        LoadStoreParameter paramStore = null;
        ProtectionParameter protParamGet = null;

        KeyStore keyStore0 = KeyStore.getInstance(keyStoreAlgorithm);
        keyStore0.load(fileInput, passwordIn);
        keyStore0.getEntry(aliasGet, protParamGet);
        // missing getKey
        keyStore0.setEntry(aliasSet, entry, protParamSet);
        keyStore0.store(paramStore);
        Assertions.notHasEnsuredPredicate(keyStore0);
        Assertions.mustNotBeInAcceptingState(keyStore0);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void keyStoreInvalidTest15()
            throws NoSuchAlgorithmException,
                    IOException,
                    KeyStoreException,
                    CertificateException,
                    UnrecoverableEntryException {

        // Related to issue 296: https://github.com/CROSSINGTUD/CryptoAnalysis/issues/296
        String aliasGet = null;
        Entry entry = null;
        InputStream fileInput = null;
        String keyStoreAlgorithm = null;
        String aliasSet = null;
        ProtectionParameter protParamSet = null;
        char[] passwordIn = null;
        LoadStoreParameter paramStore = null;
        ProtectionParameter protParamGet = null;

        KeyStore keyStore0 = KeyStore.getInstance(keyStoreAlgorithm, (Provider) null);
        keyStore0.load(fileInput, passwordIn);
        keyStore0.getEntry(aliasGet, protParamGet);
        // missing getKey
        keyStore0.setEntry(aliasSet, entry, protParamSet);
        keyStore0.store(paramStore);
        Assertions.notHasEnsuredPredicate(keyStore0);
        Assertions.mustNotBeInAcceptingState(keyStore0);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void keyStoreInvalidTest16()
            throws NoSuchAlgorithmException,
                    IOException,
                    KeyStoreException,
                    CertificateException,
                    UnrecoverableEntryException {

        // Related to issue 296: https://github.com/CROSSINGTUD/CryptoAnalysis/issues/296
        String aliasGet = null;
        Entry entry = null;
        LoadStoreParameter paramLoad = null;
        String keyStoreAlgorithm = null;
        String aliasSet = null;
        ProtectionParameter protParamSet = null;
        LoadStoreParameter paramStore = null;
        ProtectionParameter protParamGet = null;

        KeyStore keyStore0 = KeyStore.getInstance(keyStoreAlgorithm);
        keyStore0.load(paramLoad);
        keyStore0.getEntry(aliasGet, protParamGet);
        // missing getKey
        keyStore0.setEntry(aliasSet, entry, protParamSet);
        keyStore0.store(paramStore);
        Assertions.notHasEnsuredPredicate(keyStore0);
        Assertions.mustNotBeInAcceptingState(keyStore0);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void keyStoreInvalidTest17()
            throws NoSuchAlgorithmException,
                    IOException,
                    KeyStoreException,
                    CertificateException,
                    UnrecoverableEntryException {

        // Related to issue 296: https://github.com/CROSSINGTUD/CryptoAnalysis/issues/296
        String aliasGet = null;
        Entry entry = null;
        InputStream fileInput = null;
        String keyStoreAlgorithm = null;
        String aliasSet = null;
        ProtectionParameter protParamSet = null;
        OutputStream fileOutput = null;
        char[] passwordOut = null;
        char[] passwordIn = null;
        ProtectionParameter protParamGet = null;

        KeyStore keyStore0 = KeyStore.getInstance(keyStoreAlgorithm);
        keyStore0.load(fileInput, passwordIn);
        keyStore0.getEntry(aliasGet, protParamGet);
        // missing getKey
        keyStore0.setEntry(aliasSet, entry, protParamSet);
        keyStore0.store(fileOutput, passwordOut);
        Assertions.notHasEnsuredPredicate(keyStore0);
        Assertions.mustNotBeInAcceptingState(keyStore0);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void messageDigestInvalidTest10() throws NoSuchAlgorithmException {
        // Related to issue 296: https://github.com/CROSSINGTUD/CryptoAnalysis/issues/296
        byte[] inByteArr = null;

        MessageDigest messageDigest0 = MessageDigest.getInstance("SHA-256");
        byte[] out = messageDigest0.digest(inByteArr);
        // update is skipped
        out = messageDigest0.digest();
        Assertions.notHasEnsuredPredicate(out);
        Assertions.mustNotBeInAcceptingState(messageDigest0);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void messageDigestInvalidTest11() throws NoSuchAlgorithmException {
        // Related to issue 296: https://github.com/CROSSINGTUD/CryptoAnalysis/issues/296
        byte[] inByteArr = null;

        MessageDigest messageDigest0 = MessageDigest.getInstance("SHA-256", (Provider) null);
        byte[] out = messageDigest0.digest(inByteArr);
        // update is skipped
        out = messageDigest0.digest();
        Assertions.notHasEnsuredPredicate(out);
        Assertions.mustNotBeInAcceptingState(messageDigest0);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void messageDigestInvalidTest12() throws NoSuchAlgorithmException, DigestException {
        // Related to issue 296: https://github.com/CROSSINGTUD/CryptoAnalysis/issues/296
        int off = 0;
        byte[] inByteArr = null;
        int len = 0;
        byte[] out = null;

        MessageDigest messageDigest0 = MessageDigest.getInstance("SHA-256");
        out = messageDigest0.digest(inByteArr);
        // update is skipped
        messageDigest0.digest(out, off, len);
        Assertions.notHasEnsuredPredicate(out);
        Assertions.mustNotBeInAcceptingState(messageDigest0);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void messageDigestInvalidTest13() throws NoSuchAlgorithmException {
        // Related to issue 296: https://github.com/CROSSINGTUD/CryptoAnalysis/issues/296
        byte[] inByteArr = null;

        MessageDigest messageDigest0 = MessageDigest.getInstance("SHA-256");
        byte[] out = messageDigest0.digest(inByteArr);
        out = messageDigest0.digest(inByteArr);
        Assertions.hasEnsuredPredicate(out);
        Assertions.mustBeInAcceptingState(messageDigest0);
    }
}
