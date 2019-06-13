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
package org.jboss.aerogear.crypto.password;

import org.jboss.aerogear.crypto.RandomUtils;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import static org.jboss.aerogear.AeroGearCrypto.DERIVED_KEY_LENGTH;
import static org.jboss.aerogear.AeroGearCrypto.ITERATIONS;
import static org.jboss.aerogear.AeroGearCrypto.MINIMUM_ITERATION;
import static org.jboss.aerogear.AeroGearCrypto.MINIMUM_SALT_LENGTH;
import static org.jboss.aerogear.crypto.Util.checkLength;
import static org.jboss.aerogear.crypto.Util.checkSize;

public class DefaultPbkdf2 implements Pbkdf2 {

    private byte[] salt;
    private SecretKeyFactory secretKeyFactory;

    public DefaultPbkdf2(SecretKeyFactory keyFactory) {
        this.secretKeyFactory = keyFactory;
    }

    @Override
    public byte[] encrypt(String password, byte[] salt, int iterations) throws InvalidKeySpecException {
        this.salt = checkLength(salt, MINIMUM_SALT_LENGTH);
        iterations = checkSize(iterations, MINIMUM_ITERATION);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, DERIVED_KEY_LENGTH);
        return secretKeyFactory.generateSecret(spec).getEncoded();
    }

    @Override
    public SecretKey generateSecretKey(String password, byte[] salt, int iterations) throws InvalidKeySpecException {
        this.salt = checkLength(salt, MINIMUM_SALT_LENGTH);
        iterations = checkSize(iterations, MINIMUM_ITERATION);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, DERIVED_KEY_LENGTH);
        return secretKeyFactory.generateSecret(spec);
    }

    @Override
    public byte[] encrypt(String password, byte[] salt) throws InvalidKeySpecException {
        return encrypt(password, salt, ITERATIONS);
    }

    @Override
    public byte[] encrypt(String password) throws InvalidKeySpecException {
        byte[] salt = RandomUtils.randomBytes();
        return encrypt(password, salt);
    }

    @Override
    public SecretKey generateSecretKey(String password) throws InvalidKeySpecException {
        byte[] salt = RandomUtils.randomBytes();
        return generateSecretKey(password, salt, ITERATIONS);
    }

    @Override
    public boolean validate(String password, byte[] encryptedPassword, byte[] salt) throws InvalidKeySpecException {
        byte[] attempt = encrypt(password, salt);
        return Arrays.equals(encryptedPassword, attempt);
    }

    public byte[] getSalt() {
        return salt;
    }
}
