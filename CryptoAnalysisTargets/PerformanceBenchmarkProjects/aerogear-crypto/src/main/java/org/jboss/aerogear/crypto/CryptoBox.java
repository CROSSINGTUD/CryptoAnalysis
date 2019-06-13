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
package org.jboss.aerogear.crypto;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.jboss.aerogear.AeroGearCrypto;
import org.jboss.aerogear.crypto.encoders.Encoder;
import org.jboss.aerogear.crypto.keys.KeyPair;
import org.jboss.aerogear.crypto.keys.PrivateKey;

import javax.crypto.KeyAgreement;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;

import static org.jboss.aerogear.AeroGearCrypto.DEFAULT_SHA_ALGORITHM;
import static org.jboss.aerogear.AeroGearCrypto.ECDH_ALGORITHM_NAME;
import static org.jboss.aerogear.AeroGearCrypto.MINIMUM_SECRET_KEY_SIZE;
import static org.jboss.aerogear.AeroGearCrypto.TAG_LENGTH;
import static org.jboss.aerogear.crypto.Util.checkLength;
import static org.jboss.aerogear.crypto.Util.newBuffer;
import static org.jboss.aerogear.crypto.Util.newByteArray;


/**
 * Class responsible for box and unbox crypto messages given the key
 */
public class CryptoBox {

    private byte[] key;
    private AEADBlockCipher cipher;
    private byte[] authData;

    public CryptoBox(){
    }
    /**
     * Initializes the box providing the secret key
     *
     * @param key byte array
     */
    public CryptoBox(byte[] key) {
        checkLength(key, MINIMUM_SECRET_KEY_SIZE);
        this.cipher = BlockCipher.getInstance();
        this.key = key;

    }

    /**
     * Initializes the box providing the secret key
     *
     * @param key reference to the PrivateKey
     */
    public CryptoBox(PrivateKey key) {
        this(key.toBytes());
    }

    /**
     * Initializes the box providing the public key
     *
     * @param key reference to the PublicKey
     */
    public CryptoBox(PublicKey key) {
        this(key.getEncoded());
    }

    /**
     * Initializes the box providing the secret key and encoder
     *
     * @param key
     * @param encoder
     */
    public CryptoBox(String key, Encoder encoder) {
        this(encoder.decode(key));
    }

    /**
     * Initializes the box providing the key pair for asymmetric encryption
     *
     * @param privateKey
     * @param publicKey
     */
    public CryptoBox(java.security.PrivateKey privateKey, PublicKey publicKey) {
        this.cipher = BlockCipher.getInstance();
        this.key = generateSecret(privateKey, publicKey);
        checkLength(key, MINIMUM_SECRET_KEY_SIZE);
    }

    /**
     * Initializes the box providing KeyPair as parameter
     * @param keyPair
     */
    public CryptoBox(KeyPair keyPair){
        this(keyPair.getPrivateKey(), keyPair.getPublicKey());
    }

    public byte[] generateSecret(java.security.PrivateKey privateKey, PublicKey publicKey) {
        MessageDigest hash = null;
        KeyAgreement keyAgree = null;
        try {
            hash = MessageDigest.getInstance(DEFAULT_SHA_ALGORITHM, AeroGearCrypto.PROVIDER);
            keyAgree = KeyAgreement.getInstance(ECDH_ALGORITHM_NAME, AeroGearCrypto.PROVIDER);
            keyAgree.init(privateKey);
            keyAgree.doPhase(publicKey, true);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Fail: ", e);
        }

        return hash.digest(keyAgree.generateSecret());
    }

    /**
     * Given the iv, encrypt the provided data
     *
     * @param iv      initialization vector
     * @param message data to be encrypted
     * @return byte array with the cipher text
     * @throws RuntimeException
     */
    public byte[] encrypt(final byte[] iv, final byte[] message) throws RuntimeException {

        AEADParameters aeadParams = new AEADParameters(
                new KeyParameter(key), TAG_LENGTH,
                iv,
                authData);

        cipher.init(true, aeadParams);
        byte[] cipherText = newBuffer(cipher.getOutputSize(message.length));
        int outputOffset = cipher.processBytes(message, 0, message.length, cipherText, 0);

        try {
            cipher.doFinal(cipherText, outputOffset);
        } catch (InvalidCipherTextException e) {
            throw new RuntimeException("Error: ", e);
        }

        return cipherText;
    }

    /**
     * Given the iv, encrypt and encode the provided data
     *
     * @param iv      initialization vector
     * @param message data to be encrypted
     * @param encoder encoder provided RAW or HEX
     * @return byte array with the cipher text
     */
    public byte[] encrypt(String iv, String message, Encoder encoder) {
        return encrypt(encoder.decode(iv), encoder.decode(message));
    }

    /**
     * Given the iv, decrypt the provided data
     *
     * @param iv         initialization vector
     * @param cipherText data to be decrypted
     * @return byte array with the plain text
     * @throws RuntimeException
     */
    public byte[] decrypt(byte[] iv, byte[] cipherText) throws RuntimeException {

        AEADParameters aeadParams = new AEADParameters(
                new KeyParameter(key), TAG_LENGTH,
                iv,
                authData);

        cipher.init(false, aeadParams);
        byte[] buffer = newByteArray(cipherText);
        byte[] plainText = newBuffer(cipher.getOutputSize(cipherText.length));
        int outputOffset = cipher.processBytes(buffer, 0, buffer.length, plainText, 0);

        try {
            cipher.doFinal(plainText, outputOffset);
        } catch (InvalidCipherTextException e) {
            throw new RuntimeException("Error: ", e);
        }

        return plainText;
    }

    /**
     * Given the iv, decrypt the provided data
     *
     * @param iv         initialization vector
     * @param cipherText data to be decrypted
     * @param encoder    encoder provided RAW or HEX
     * @return byte array with the plain text
     */
    public byte[] decrypt(String iv, String cipherText, Encoder encoder) {
        return decrypt(encoder.decode(iv), encoder.decode(cipherText));
    }
}
