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
import org.jboss.aerogear.crypto.password.Pbkdf2;
import org.junit.Test;

import java.util.Arrays;

import static org.jboss.aerogear.crypto.encoders.Encoder.HEX;
import static org.jboss.aerogear.fixture.TestVectors.BOB_PRIVATE_KEY;
import static org.jboss.aerogear.fixture.TestVectors.PASSWORD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PrivateKeyTest {

    @Test
    public void testGeneratePrivateKey() {
        try {
            new PrivateKey();
        } catch (Exception e) {
            fail("Should not raise any exception and generate the private key");
        }
    }

    @Test
    public void testAcceptsRawValidKey() throws Exception {
        try {
            byte[] rawKey = HEX.decode(BOB_PRIVATE_KEY);
            new PrivateKey(rawKey);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should return a valid key size");
        }
    }

    @Test
    public void testAcceptsPasswordBasedValidKey() throws Exception {
        try {
            Pbkdf2 pbkdf2 = AeroGearCrypto.pbkdf2();
            byte[] rawPassword = pbkdf2.encrypt(PASSWORD);
            new PrivateKey(rawPassword);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should return a valid key size");
        }
    }

    @Test
    public void testAcceptsHexValidKey() throws Exception {
        try {
            new PrivateKey(BOB_PRIVATE_KEY, HEX);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should return a valid key size");
        }
    }

    @Test
    public void testCreateHexValidKey() throws Exception {
        try {
            new PrivateKey(BOB_PRIVATE_KEY, HEX).toString();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should return a valid key size");
        }
    }

    @Test
    public void testCreateByteValidKey() throws Exception {
        try {
            new PrivateKey(BOB_PRIVATE_KEY, HEX).toBytes();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should return a valid key size");
        }
    }

    @Test(expected = RuntimeException.class)
    public void testRejectNullKey() throws Exception {
        byte[] key = null;
        new PrivateKey(key);
        fail("Should reject null keys");
    }

    @Test(expected = RuntimeException.class)
    public void testRejectShortKey() throws Exception {
        byte[] key = "short".getBytes();
        new PrivateKey(key);
        fail("Should reject short keys");
    }

    @Test
    public void testPrivateKeyToString() throws Exception {
        try {
            PrivateKey key = new PrivateKey(BOB_PRIVATE_KEY, HEX);
            assertEquals("Correct private key expected", BOB_PRIVATE_KEY, key.toString());
        } catch (Exception e) {
            fail("Should return a valid key size");
        }
    }

    @Test
    public void testPrivateKeyToBytes() throws Exception {
        try {
            PrivateKey key = new PrivateKey(BOB_PRIVATE_KEY, HEX);
            assertTrue("Correct private key expected", Arrays.equals(HEX.decode(BOB_PRIVATE_KEY),
                    key.toBytes()));
        } catch (Exception e) {
            fail("Should return a valid key size");
        }
    }
}
