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
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class RandomUtilsTest {
    @Test
    public void testProducesRandomBytes() throws Exception {
        final int size = 16;
        assertEquals("Invalid random bytes", size, RandomUtils.randomBytes(size).length);
    }

    @Test
    public void testProducesDefaultRandomBytes() throws Exception {
        final int size = 16;
        assertEquals("Invalid random bytes", size, RandomUtils.randomBytes().length);
    }

    @Test
    public void testProducesDifferentRandomBytes() throws Exception {
        final int size = 16;
        assertFalse("Should produce different random bytes", Arrays.equals(RandomUtils.randomBytes(size), new RandomUtils().randomBytes(size)));
    }

    @Test
    public void testProducesDifferentDefaultRandomBytes() throws Exception {
        final int size = 32;
        assertFalse("Should produce different random bytes", Arrays.equals(RandomUtils.randomBytes(), new RandomUtils().randomBytes(size)));
    }

    @Test
    public void testProducesRandomString() throws Exception {
        final int size = 32;
        final int expectedSize = 44;
        assertEquals("Invalid random bytes", expectedSize, RandomUtils.randomBytes(size, Encoder.BASE64).length());
    }
}
