package org.jboss.aerogear.crypto;

import org.jboss.aerogear.crypto.signature.SigningKey;
import org.jboss.aerogear.crypto.signature.VerifyKey;
import org.junit.Test;

import static org.jboss.aerogear.crypto.encoders.Encoder.HEX;
import static org.jboss.aerogear.fixture.TestVectors.SIGN_MESSAGE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SigningKeyTest {

    @Test
    public void testGenerateSigninKey() throws Exception {
        try {
            new SigningKey();
        } catch (Exception e) {
            fail("Should return a valid key size");
        }
    }

    @Test
    public void testSignMessageAsBytes() throws Exception {
        SigningKey key = new SigningKey();
        byte[] signMessage = HEX.decode(SIGN_MESSAGE);
        byte[] signedMessage = key.sign(HEX.decode(SIGN_MESSAGE));
        VerifyKey verifyKey = new VerifyKey(key.getPublicKey());
        Boolean isValid = verifyKey.verify(signMessage, signedMessage);
        assertTrue("Message sign has failed", isValid);
    }

    @Test
    public void testSignMessageAsHex() throws Exception {
        SigningKey key = new SigningKey();
        String signedMessage = key.sign(SIGN_MESSAGE, HEX);
        VerifyKey verifyKey = new VerifyKey(key.getPublicKey());
        Boolean isValid = verifyKey.verify(SIGN_MESSAGE, signedMessage, HEX);
        assertTrue("Message sign has failed", isValid);
    }

    @Test(expected = RuntimeException.class)
    public void testVerifyCorruptedSignature() throws Exception {
        SigningKey key = new SigningKey();
        byte[] signMessage = HEX.decode(SIGN_MESSAGE);
        byte[] corruptedSignature = key.sign(signMessage);
        corruptedSignature[0] = ' ';
        VerifyKey verifyKey = new VerifyKey(key.getPublicKey());
        Boolean isValid = verifyKey.verify(signMessage, corruptedSignature);
        assertTrue("Message sign has failed", isValid);
    }

    @Test
    public void testVerifyCorruptedMessage() throws Exception {
        SigningKey key = new SigningKey();
        byte[] signMessage = HEX.decode(SIGN_MESSAGE);
        byte[] signature = key.sign(HEX.decode(SIGN_MESSAGE));
        signMessage[0] = ' ';
        VerifyKey verifyKey = new VerifyKey(key.getPublicKey());
        Boolean isValid = verifyKey.verify(signMessage, signature);
        assertFalse("Message should be invalid", isValid);
    }
}
