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

import org.jboss.aerogear.AeroGearCrypto;
import org.jboss.aerogear.crypto.keys.KeyPair;
import org.jboss.aerogear.crypto.keys.PrivateKey;
import org.jboss.aerogear.crypto.password.Pbkdf2;
import org.junit.Test;

import java.util.Arrays;

import static org.jboss.aerogear.crypto.encoders.Encoder.HEX;
import static org.jboss.aerogear.crypto.encoders.Encoder.RAW;
import static org.jboss.aerogear.fixture.TestVectors.ALICE_PRIVATE_KEY;
import static org.jboss.aerogear.fixture.TestVectors.ALICE_PUBLIC_KEY;
import static org.jboss.aerogear.fixture.TestVectors.BOB_PRIVATE_KEY;
import static org.jboss.aerogear.fixture.TestVectors.BOB_PUBLIC_KEY;
import static org.jboss.aerogear.fixture.TestVectors.BOB_SECRET_KEY;
import static org.jboss.aerogear.fixture.TestVectors.BOX_MESSAGE;
import static org.jboss.aerogear.fixture.TestVectors.BOX_NONCE;
import static org.jboss.aerogear.fixture.TestVectors.BOX_STRING_MESSAGE;
import static org.jboss.aerogear.fixture.TestVectors.CRYPTOBOX_CIPHERTEXT;
import static org.jboss.aerogear.fixture.TestVectors.CRYPTOBOX_IV;
import static org.jboss.aerogear.fixture.TestVectors.CRYPTOBOX_MESSAGE;
import static org.jboss.aerogear.fixture.TestVectors.PASSWORD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CryptoBoxTest {

    @Test
    public void testAcceptStrings() throws Exception {
        try {
            new CryptoBox(BOB_SECRET_KEY, HEX);
        } catch (Exception e) {
            fail("CryptoBox should accept strings");
        }
    }

    @Test
    public void testAcceptPrivateKey() throws Exception {
        try {
            new CryptoBox(new PrivateKey(BOB_SECRET_KEY));
        } catch (Exception e) {
            fail("CryptoBox should accept key pairs");
        }
    }

    @Test
    public void testAcceptPasswordBasedPrivateKey() throws Exception {
        try {
            Pbkdf2 pbkdf2 = AeroGearCrypto.pbkdf2();
            byte[] rawPassword = pbkdf2.encrypt(PASSWORD);
            new CryptoBox(new PrivateKey(rawPassword));
        } catch (Exception e) {
            fail("CryptoBox should accept key pairs");
        }
    }

    @Test(expected = RuntimeException.class)
    public void testNullPrivateKey() throws Exception {
        String key = null;
        new CryptoBox(new PrivateKey(key));
        fail("Should raise an exception");
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidPrivateKey() throws Exception {
        String key = "hello";
        new CryptoBox(new PrivateKey(key));
        fail("Should raise an exception");
    }

    @Test
    public void testEncryptRawBytes() throws Exception {
        CryptoBox cryptoBox = new CryptoBox(new PrivateKey(BOB_SECRET_KEY));
        byte[] IV = HEX.decode(CRYPTOBOX_IV);
        byte[] message = HEX.decode(CRYPTOBOX_MESSAGE);
        byte[] ciphertext = HEX.decode(CRYPTOBOX_CIPHERTEXT);
        byte[] result = cryptoBox.encrypt(IV, message);
        assertTrue("failed to generate ciphertext", Arrays.equals(result, ciphertext));
    }

    @Test
    public void testDecryptRawBytes() throws Exception {
        CryptoBox cryptoBox = new CryptoBox(new PrivateKey(BOB_SECRET_KEY));
        byte[] IV = HEX.decode(CRYPTOBOX_IV);
        byte[] expectedMessage = HEX.decode(CRYPTOBOX_MESSAGE);
        byte[] ciphertext = cryptoBox.encrypt(IV, expectedMessage);

        CryptoBox pandora = new CryptoBox(new PrivateKey(BOB_SECRET_KEY));
        byte[] message = pandora.decrypt(IV, ciphertext);
        assertTrue("failed to decrypt ciphertext", Arrays.equals(message, expectedMessage));
    }


    @Test
    public void testEncrypAndDecryptString() {
        CryptoBox cryptoBox = new CryptoBox(new PrivateKey(BOB_SECRET_KEY));
        byte[] IV = HEX.decode(CRYPTOBOX_IV);
        byte[] cipherText = cryptoBox.encrypt(IV, BOX_STRING_MESSAGE.getBytes());

        CryptoBox pandora = new CryptoBox(new PrivateKey(BOB_SECRET_KEY));
        byte[] message = pandora.decrypt(IV, cipherText);
        assertEquals("decrypted message should equals the message", RAW.encode(message), BOX_STRING_MESSAGE);
    }

    @Test
    public void testPasswordBasedKeyDecryptRawBytes() throws Exception {
        Pbkdf2 pbkdf2 = AeroGearCrypto.pbkdf2();
        byte[] rawPassword = pbkdf2.encrypt(PASSWORD);
        PrivateKey privateKey = new PrivateKey(rawPassword);

        CryptoBox cryptoBox = new CryptoBox(privateKey);
        byte[] IV = HEX.decode(CRYPTOBOX_IV);
        byte[] expectedMessage = HEX.decode(CRYPTOBOX_MESSAGE);
        byte[] ciphertext = cryptoBox.encrypt(IV, expectedMessage);

        CryptoBox pandora = new CryptoBox(privateKey);
        byte[] message = pandora.decrypt(IV, ciphertext);
        assertTrue("failed to decrypt ciphertext", Arrays.equals(message, expectedMessage));
    }

    @Test
    public void testEncryptHexBytes() throws Exception {
        CryptoBox cryptoBox = new CryptoBox(new PrivateKey(BOB_SECRET_KEY));
        byte[] ciphertext = HEX.decode(CRYPTOBOX_CIPHERTEXT);

        byte[] result = cryptoBox.encrypt(CRYPTOBOX_IV, CRYPTOBOX_MESSAGE, HEX);
        assertTrue("failed to generate ciphertext", Arrays.equals(result, ciphertext));
    }

    @Test
    public void testDecryptHexBytes() throws Exception {
        CryptoBox cryptoBox = new CryptoBox(new PrivateKey(BOB_SECRET_KEY));
        byte[] expectedMessage = HEX.decode(CRYPTOBOX_MESSAGE);
        byte[] ciphertext = cryptoBox.encrypt(CRYPTOBOX_IV, CRYPTOBOX_MESSAGE, HEX);

        CryptoBox pandora = new CryptoBox(new PrivateKey(BOB_SECRET_KEY));
        byte[] message = pandora.decrypt(CRYPTOBOX_IV, HEX.encode(ciphertext), HEX);
        assertTrue("failed to decrypt ciphertext", Arrays.equals(message, expectedMessage));
    }

    @Test(expected = RuntimeException.class)
    public void testDecryptCorruptedCipherText() throws Exception {
        CryptoBox cryptoBox = new CryptoBox(new PrivateKey(BOB_SECRET_KEY));
        byte[] IV = HEX.decode(CRYPTOBOX_IV);
        byte[] message = HEX.decode(CRYPTOBOX_MESSAGE);
        byte[] ciphertext = cryptoBox.encrypt(IV, message);
        ciphertext[23] = ' ';

        CryptoBox pandora = new CryptoBox(new PrivateKey(BOB_SECRET_KEY));
        pandora.decrypt(IV, ciphertext);
        fail("Should raise an exception");
    }

    @Test(expected = RuntimeException.class)
    public void testDecryptCorruptedIV() throws Exception {
        CryptoBox cryptoBox = new CryptoBox(new PrivateKey(BOB_PRIVATE_KEY));
        byte[] IV = HEX.decode(CRYPTOBOX_IV);
        byte[] message = HEX.decode(CRYPTOBOX_MESSAGE);
        byte[] ciphertext = cryptoBox.encrypt(IV, message);
        IV[23] = ' ';

        CryptoBox pandora = new CryptoBox(new PrivateKey(BOB_PRIVATE_KEY));
        pandora.decrypt(IV, ciphertext);
        fail("Should raise an exception");
    }

    @Test
    public void testAcceptPublicPrivateKeys() throws Exception {
        try {
            KeyPair keyPair = new KeyPair();
            new CryptoBox(keyPair.getPrivateKey(), keyPair.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Box should accept key pairs");
        }
    }

    @Test
    public void testAcceptKeyPair() throws Exception {
        try {
            KeyPair keyPair = new KeyPair();
            new CryptoBox(keyPair);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Box should accept key pairs");
        }
    }


    @Test(expected = RuntimeException.class)
    public void testNullPublicKey() throws Exception {
        KeyPair keyPair = new KeyPair();
        new CryptoBox(keyPair.getPrivateKey(), null);
        fail("Should raise an exception");
    }

    @Test(expected = RuntimeException.class)
    public void testNullSecretKey() throws Exception {
        KeyPair keyPair = new KeyPair();
        new CryptoBox(null, keyPair.getPublicKey());
        fail("Should raise an exception");
    }

    @Test
    public void testAsymmetricDecryptRawBytes() throws Exception {
        KeyPair keyPair = new KeyPair();
        KeyPair keyPairPandora = new KeyPair();

        CryptoBox cryptoBox = new CryptoBox(keyPair.getPrivateKey(), keyPairPandora.getPublicKey());
        byte[] IV = HEX.decode(BOX_NONCE);
        byte[] expectedMessage = HEX.decode(BOX_MESSAGE);
        byte[] ciphertext = cryptoBox.encrypt(IV, expectedMessage);

        CryptoBox pandora = new CryptoBox(keyPairPandora.getPrivateKey(), keyPair.getPublicKey());
        byte[] message = pandora.decrypt(IV, ciphertext);
        assertTrue("failed to decrypt ciphertext", Arrays.equals(message, expectedMessage));
    }


    @Test
    public void testAsymmetricDecryptionWithRawKeys() throws Exception {
        byte[] bobPrivateKey = HEX.decode(BOB_PRIVATE_KEY);
        byte[] bobPublicKey = HEX.decode(BOB_PUBLIC_KEY);
        byte[] alicePrivateKey = HEX.decode(ALICE_PRIVATE_KEY);
        byte[] alicePublicKey = HEX.decode(ALICE_PUBLIC_KEY);

        KeyPair cryptoBoxKeyPair = new KeyPair(bobPrivateKey, alicePublicKey);

        CryptoBox cryptoBox = new CryptoBox(cryptoBoxKeyPair.getPrivateKey(), cryptoBoxKeyPair.getPublicKey());
        byte[] IV = HEX.decode(BOX_NONCE);
        byte[] expectedMessage = HEX.decode(BOX_MESSAGE);
        byte[] ciphertext = cryptoBox.encrypt(IV, expectedMessage);

        KeyPair pandoraBoxKeyPair = new KeyPair(alicePrivateKey, bobPublicKey);

        CryptoBox pandora = new CryptoBox(pandoraBoxKeyPair.getPrivateKey(), pandoraBoxKeyPair.getPublicKey());
        byte[] message = pandora.decrypt(IV, ciphertext);
        assertTrue("failed to decrypt ciphertext", Arrays.equals(message, expectedMessage));
    }

    @Test
    public void testAsymmetricDecryptionWithRawKeysAndEncoderProvided() throws Exception {

        KeyPair cryptoBoxKeyPair = new KeyPair(BOB_PRIVATE_KEY, ALICE_PUBLIC_KEY, HEX);

        CryptoBox cryptoBox = new CryptoBox(cryptoBoxKeyPair.getPrivateKey(), cryptoBoxKeyPair.getPublicKey());
        byte[] IV = HEX.decode(BOX_NONCE);
        byte[] expectedMessage = HEX.decode(BOX_MESSAGE);
        byte[] ciphertext = cryptoBox.encrypt(IV, expectedMessage);

        KeyPair pandoraBoxKeyPair = new KeyPair(ALICE_PRIVATE_KEY, BOB_PUBLIC_KEY, HEX);

        CryptoBox pandora = new CryptoBox(pandoraBoxKeyPair.getPrivateKey(), pandoraBoxKeyPair.getPublicKey());
        byte[] message = pandora.decrypt(IV, ciphertext);
        assertTrue("failed to decrypt ciphertext", Arrays.equals(message, expectedMessage));
    }

    @Test(expected = RuntimeException.class)
    public void testAsymmetricDecryptCorruptedCipherText() throws Exception {
        KeyPair keyPair = new KeyPair();
        CryptoBox box = new CryptoBox(keyPair.getPrivateKey(), keyPair.getPublicKey());
        byte[] nonce = HEX.decode(BOX_NONCE);
        byte[] message = HEX.decode(BOX_MESSAGE);
        byte[] ciphertext = box.encrypt(nonce, message);
        ciphertext[23] = ' ';

        KeyPair keyPairPandora = new KeyPair();
        CryptoBox pandora = new CryptoBox(keyPairPandora.getPrivateKey(), keyPairPandora.getPublicKey());
        pandora.decrypt(nonce, ciphertext);
        fail("Should raise an exception");
    }
}
