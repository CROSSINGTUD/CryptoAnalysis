/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aerogear.crypto.keys;

import org.jboss.aerogear.AeroGearCrypto;
import org.jboss.aerogear.crypto.encoders.Encoder;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static org.jboss.aerogear.AeroGearCrypto.DEFAULT_CURVE_NAME;
import static org.jboss.aerogear.AeroGearCrypto.ECDH_ALGORITHM_NAME;


/**
 * Represents a pair of cryptographic keys (a public and a private key) used for asymmetric encryption
 */
public class KeyPair {

    private java.security.PublicKey publicKey;
    private java.security.PrivateKey privateKey;

    public KeyPair(String algorithm, String curveName) {

        java.security.KeyPair keyPair = null;
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithm, AeroGearCrypto.PROVIDER);
            ECGenParameterSpec ecSpec = new ECGenParameterSpec(curveName);
            keyGen.initialize(ecSpec, new SecureRandom());
            keyPair = keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }

    //ASN.1 key pair encoding
    public KeyPair(byte[] privateKey, byte[] publicKey) {

        try {
            final KeyFactory keyFactory = KeyFactory.getInstance(ECDH_ALGORITHM_NAME, AeroGearCrypto.PROVIDER);
            final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
            final X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
            this.publicKey = keyFactory.generatePublic(x509KeySpec);
            this.privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    public KeyPair(String privateKey, String publicKey, Encoder encoder){
        this(encoder.decode(privateKey), encoder.decode(publicKey));
    }

    /**
     * Initialize the key pair with the standard curve name
     */
    public KeyPair() {
        this(ECDH_ALGORITHM_NAME, DEFAULT_CURVE_NAME);
    }


    /**
     * Access to the public key
     *
     * @return the reference to the public key
     */
    public java.security.PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * Access to the private key
     *
     * @return the reference to the private key
     */
    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
