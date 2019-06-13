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

import javax.crypto.SecretKey;
import java.security.spec.InvalidKeySpecException;

/**
 * Provides a level of abstraction for PBKDF2
 */
public interface Pbkdf2 {

    /**
     * Generate the secret key
     * @param password
     * @param salt
     * @param iterations
     * @return secret key
     * @throws InvalidKeySpecException
     */
    byte[] encrypt(String password, byte[] salt, int iterations) throws InvalidKeySpecException;

    /**
     * Generate the secret key
     * @param password
     * @param salt
     * @return secret key
     * @throws InvalidKeySpecException
     */
    byte[] encrypt(String password, byte[] salt) throws InvalidKeySpecException;

    /**
     * Generate the secret key
     * @param password
     * @return secret key
     * @throws InvalidKeySpecException
     */
    byte[] encrypt(String password) throws InvalidKeySpecException;

    /**
     * Validate the generated secret key
     * @param password
     * @param encryptedPassword
     * @param salt
     * @return secret key
     * @throws InvalidKeySpecException
     */
    boolean validate(String password, byte[] encryptedPassword, byte[] salt) throws InvalidKeySpecException;

    public SecretKey generateSecretKey(String password, byte[] salt, int iterations) throws InvalidKeySpecException;

    public SecretKey generateSecretKey(String password) throws InvalidKeySpecException;

}
