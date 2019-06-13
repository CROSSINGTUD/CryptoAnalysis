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

import org.jboss.aerogear.crypto.encoders.Encoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {

    public static final String DEFAULT_ALGORITHM = "SHA-256";
    private MessageDigest hashDigest;

    public Hash(String algorithm) {
        try {
            this.hashDigest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithm not found: " + algorithm);
        }
    }

    public Hash() {
        this(DEFAULT_ALGORITHM);
    }

    public String digest(byte[] message, Encoder encoder) throws NoSuchAlgorithmException {
        return encoder.encode(hashDigest.digest(message));
    }

    public String digest(String message, Encoder encoder) throws NoSuchAlgorithmException {
        return digest(message.getBytes(), encoder);
    }


    public byte[] digest(byte[] message) throws NoSuchAlgorithmException {
        return hashDigest.digest(message);
    }


}
