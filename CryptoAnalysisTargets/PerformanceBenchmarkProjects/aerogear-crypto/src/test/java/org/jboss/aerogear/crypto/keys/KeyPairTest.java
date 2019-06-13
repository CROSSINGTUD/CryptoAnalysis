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

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class KeyPairTest {

    @Test
    public void testGenerateKeyPair() {
        try {
            KeyPair key = new KeyPair();
            assertNotNull(key.getPrivateKey());
            assertNotNull(key.getPublicKey());
        } catch (Exception e) {
            fail("Should return a valid key size");
        }
    }

    @Test
    public void testGeneratePublicKey() throws Exception {
        try {
            KeyPair key = new KeyPair();
            assertNotNull(key.getPublicKey());
        } catch (Exception e) {
            fail("Should return a valid key size");
        }
    }
}
