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

import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;

import static org.jboss.aerogear.AeroGearCrypto.Mode;
import static org.jboss.aerogear.AeroGearCrypto.Mode.GCM;

/**
 * Representation of the cipher modes supported
 */
public class BlockCipher {

    private BlockCipher() {
    }

    public static AEADBlockCipher getInstance() {
        return getNewCipher(GCM);
    }

    /**
     * Retrieve a new instance of the block mode provided
     * @param blockMode block mode name
     * @return instance to the block mode
     */
    public static AEADBlockCipher getNewCipher(Mode blockMode) {

        AESEngine aesEngine = new AESEngine();

        switch (blockMode) {

        case GCM:
            return new GCMBlockCipher(aesEngine);
        default:
            throw new RuntimeException("Block cipher not found");
        }
    }

    /**
     * Generates a non-predictable initialization vector
     * @return byte array with the initialization vector generated
     */
    public static byte[] getIV() {
        return RandomUtils.randomBytes();
    }

}
