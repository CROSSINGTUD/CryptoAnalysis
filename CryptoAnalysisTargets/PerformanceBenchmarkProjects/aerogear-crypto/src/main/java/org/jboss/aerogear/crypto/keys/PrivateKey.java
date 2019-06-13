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

import org.jboss.aerogear.crypto.encoders.Encoder;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;

import static org.jboss.aerogear.AeroGearCrypto.Algorithm.AES;
import static org.jboss.aerogear.AeroGearCrypto.MINIMUM_SECRET_KEY_SIZE;
import static org.jboss.aerogear.crypto.Util.checkLength;
import static org.jboss.aerogear.crypto.encoders.Encoder.HEX;

/**
 * Represents the private key used for symmetric encryption
 */
public class PrivateKey {

    private byte[] secretKey;

    /**
     * Initialize and generate the private key with the default key size
     */
    public PrivateKey() {
        KeyGenerator kGen;
        try {
            kGen = KeyGenerator.getInstance(AES.toString());
            kGen.init(AES.getKeySize());
            this.secretKey = kGen.generateKey().getEncoded();
            checkLength(secretKey, MINIMUM_SECRET_KEY_SIZE);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error: ", e);
        }
    }

    /**
     * Initializes the private key with the provided bytes
     *
     * @param keyBytes provided key
     */
    public PrivateKey(byte[] keyBytes) {
        checkLength(keyBytes, MINIMUM_SECRET_KEY_SIZE);
        this.secretKey = keyBytes;
    }

    /**
     * Initializes the private key with the provided string
     *
     * @param secretKey provided key
     */
    public PrivateKey(String secretKey) {
        this.secretKey = HEX.decode(secretKey);
        checkLength(this.secretKey, MINIMUM_SECRET_KEY_SIZE);
    }

    /**
     * Initializes the private key with the provided string and encoder
     *
     * @param secretKey provided key
     * @param encoder   provided encoder
     */
    public PrivateKey(String secretKey, Encoder encoder) {
        checkLength(encoder.decode(secretKey), MINIMUM_SECRET_KEY_SIZE);
        this.secretKey = encoder.decode(secretKey);
    }

    /**
     * Retrieve the private key
     *
     * @return sequence of bytes representing the private key
     */
    public byte[] toBytes() {
        return secretKey;
    }

    /**
     * Retrieve the hexadecimal representation of the key
     *
     * @return key encoded to hexadecimal by default
     */
    @Override
    public String toString() {
        return HEX.encode(secretKey);
    }
}
