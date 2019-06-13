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
package org.jboss.aerogear;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jboss.aerogear.crypto.Util;
import org.jboss.aerogear.crypto.password.DefaultPbkdf2;
import org.jboss.aerogear.crypto.password.Pbkdf2;

import javax.crypto.SecretKeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

/**
 * Provides constants and static factories to be used inside the project
 */
public class AeroGearCrypto {

    public static final String PROVIDER = Util.isAndroid() ? "SC" : "BC";
    //PBKDF2
    public static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";
    public static final int DERIVED_KEY_LENGTH = 256;
    public static final int ITERATIONS = 20000;
    public static final int MINIMUM_SALT_LENGTH = 16;
    public static final int MINIMUM_ITERATION = 10000;
    //AES
    public static final int MINIMUM_SECRET_KEY_SIZE = 32;
    //GCM
    public static final int TAG_LENGTH = 128;
    //HMAC
    public static final String HMAC_ALGORITHM = "HmacSHA256";
    //ECDH
    public static final String ECDH_ALGORITHM_NAME = "ECDH";
    //ECDSA
    public static final String ECDSA_ALGORITHM_NAME = "ECDSA";
    //Default Curve Name
    public static final String DEFAULT_CURVE_NAME = "P-256";
    //ECDSA SHA
    public static final String DEFAULT_ECDSA_SHA = "SHA256withECDSA";
    //Default SHA
    public static final String DEFAULT_SHA_ALGORITHM = "SHA-256";

    private AeroGearCrypto() {
    }

    static {
        if (Security.getProvider(PROVIDER) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

    }

    public static Pbkdf2 pbkdf2() {
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            return new DefaultPbkdf2(keyFactory);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Representation of the algorithms supported
     */
    public enum Algorithm {

        AES("AES", 256);

        private String name;
        private int keySize;

        Algorithm(String name, int keySize) {
            this.name = name;
            this.keySize = keySize;
        }

        /**
         * Algorithm name
         *
         * @return string representation of the algorithm name
         */
        @Override
        public String toString() {
            return name;
        }

        /**
         * Key size
         *
         * @return integer representation of the key size
         */
        public int getKeySize() {
            return keySize;
        }

    }

    /**
     * Padding schemes supported
     */
    public enum Padding {

        NONE("NoPadding"),
        PKCS7("PKCS7Padding");

        private final String padding;

        Padding(String padding) {
            this.padding = padding;
        }

        @Override
        public String toString() {
            return padding;
        }
    }

    /**
     * Block modes supported
     */
    public enum Mode {
        GCM("GCM", Padding.NONE);
        private final Padding padding;
        private String mode;

        private Mode(String mode, Padding padding) {
            this.mode = mode;
            this.padding = padding;
        }

        @Override
        public String toString() {
            return String.format("%s/%s", mode, padding);
        }
    }

}
