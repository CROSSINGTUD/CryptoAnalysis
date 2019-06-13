/**
 * Copyright 2013 Bruno Oliveira, and individual contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.aerogear.crypto;

import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertTrue;
import static org.jboss.aerogear.crypto.encoders.Encoder.HEX;
import static org.jboss.aerogear.fixture.TestVectors.SHA256_DIGEST;
import static org.jboss.aerogear.fixture.TestVectors.SHA256_DIGEST_EMPTY_STRING;
import static org.jboss.aerogear.fixture.TestVectors.SHA256_MESSAGE;
import static org.jboss.aerogear.fixture.TestVectors.SHA512_DIGEST;
import static org.jboss.aerogear.fixture.TestVectors.SHA512_DIGEST_EMPTY_STRING;
import static org.jboss.aerogear.fixture.TestVectors.SHA512_MESSAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class HashTest {

    private Hash hash = new Hash();

    @Test
    public void testSha256() throws Exception {
        final byte[] rawMessage = SHA256_MESSAGE.getBytes();
        String result = HEX.encode(hash.digest(rawMessage));
        assertTrue("Hash is invalid", Arrays.equals(SHA256_DIGEST.getBytes(), result.getBytes()));
    }

    @Test
    public void testSha256EmptyString() throws Exception {
        byte[] result = hash.digest("".getBytes());
        assertEquals("Hash is invalid", SHA256_DIGEST_EMPTY_STRING, HEX.encode(result));
    }

    @Test
    public void testSha256HexString() throws Exception {
        String result = hash.digest(SHA256_MESSAGE, HEX);
        assertEquals("Hash is invalid", SHA256_DIGEST, result);
    }

    @Test
    public void testSha256EmptyHexString() throws Exception {
        String result = hash.digest("", HEX);
        assertEquals("Hash is invalid", SHA256_DIGEST_EMPTY_STRING, result);
    }

    @Test
    public void testSha256NullByte() {
        try {
            hash.digest("\0".getBytes());
        } catch (Exception e) {
            fail("Should not raise any exception on null byte");
        }
    }

    @Test
    public void testSha512() throws Exception {
        final byte[] rawMessage = SHA512_MESSAGE.getBytes();
        hash = new Hash("SHA-512");
        String result = HEX.encode(hash.digest(rawMessage));
        assertTrue("Hash is invalid", Arrays.equals(SHA512_DIGEST.getBytes(), result.getBytes()));
    }

    @Test
    public void testSha512EmptyString() throws Exception {
        hash = new Hash("SHA-512");
        byte[] result = hash.digest("".getBytes());
        assertEquals("Hash is invalid", SHA512_DIGEST_EMPTY_STRING, HEX.encode(result));
    }

    @Test
    public void testSha512HexString() throws Exception {
        hash = new Hash("SHA-512");
        String result = hash.digest(SHA512_MESSAGE, HEX);
        assertEquals("Hash is invalid", SHA512_DIGEST, result);
    }

    @Test
    public void testSha512EmptyHexString() throws Exception {
        hash = new Hash("SHA-512");
        String result = hash.digest("", HEX);
        assertEquals("Hash is invalid", SHA512_DIGEST_EMPTY_STRING, result);
    }

    @Test
    public void testSha512NullByte() {
        try {
            hash.digest("\0".getBytes());
        } catch (Exception e) {
            fail("Should not raise any exception on null byte");
        }
    }
}
