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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Provides a cryptographically strong RNG
 */
public class RandomUtils {

    private static SecureRandom secureRandom;
    private static final String ALGORITHM = "SHA1PRNG";

    /**
     * Generates a number random bytes which defaults to the buffer of 16
     *
     * @return byte array representation of random bytes
     */
    public static byte[] randomBytes() {
        return randomBytes(16);
    }

    /**
     * Generates a number random bytes specified by the user
     *
     * @return byte array representation of random bytes
     */
    public static byte[] randomBytes(int n) {
        byte[] buffer = new byte[n];
        if (RandomUtils.secureRandom == null) {
            try {
                RandomUtils.secureRandom = SecureRandom.getInstance(ALGORITHM);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        RandomUtils.secureRandom.nextBytes(buffer);
        return buffer;
    }

    /**
     * Generates a number random bytes specified by the user
     *
     * @return byte array representation of random bytes
     */
    public static String randomBytes(int n, Encoder encoder) {
        return encoder.encode(randomBytes(n));
    }

    /**
     * Retrieve the reference to the SecureRandom object
     *
     * @return SecureRandom
     */
    public SecureRandom getSecureRandom() {
        byte[] buffer = new byte[16];
        RandomUtils.secureRandom.nextBytes(buffer);
        return secureRandom;
    }
}
