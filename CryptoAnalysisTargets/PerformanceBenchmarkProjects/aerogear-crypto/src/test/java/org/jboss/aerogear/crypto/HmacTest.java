package org.jboss.aerogear.crypto;

import org.jboss.aerogear.AeroGearCrypto;
import org.jboss.aerogear.crypto.password.Pbkdf2;
import org.junit.Test;

import javax.crypto.SecretKey;

import static org.jboss.aerogear.fixture.TestVectors.HMAC_STRING_DIGEST_SHA1;
import static org.jboss.aerogear.fixture.TestVectors.HMAC_STRING_DIGEST_SHA256;
import static org.jboss.aerogear.fixture.TestVectors.HMAC_STRING_DIGEST_SHA512;
import static org.jboss.aerogear.fixture.TestVectors.HMAC_STRING_MESSAGE;
import static org.jboss.aerogear.fixture.TestVectors.HMAC_STRING_SALT;
import static org.jboss.aerogear.fixture.TestVectors.PASSWORD;
import static org.junit.Assert.assertEquals;

public class HmacTest {

    @Test
    public void testSHA1HmacDigest() throws Exception {
        Pbkdf2 pbkdf2 = AeroGearCrypto.pbkdf2();
        byte[] salt = HMAC_STRING_SALT.getBytes();
        int iterations = 100000;
        SecretKey secretKey = pbkdf2.generateSecretKey(PASSWORD, salt, iterations);
        Hmac hmac = new Hmac("HmacSha1", secretKey);
        assertEquals(HMAC_STRING_DIGEST_SHA1, hmac.digest(HMAC_STRING_MESSAGE.getBytes()));
    }

    @Test
    public void testSHA256HmacDigest() throws Exception {
        Pbkdf2 pbkdf2 = AeroGearCrypto.pbkdf2();
        byte[] salt = HMAC_STRING_SALT.getBytes();
        int iterations = 100000;
        SecretKey secretKey = pbkdf2.generateSecretKey(PASSWORD, salt, iterations);
        Hmac hmac = new Hmac(secretKey);
        assertEquals(HMAC_STRING_DIGEST_SHA256, hmac.digest(HMAC_STRING_MESSAGE.getBytes()));
    }


    @Test
    public void testSHA512HmacDigest() throws Exception {
        Pbkdf2 pbkdf2 = AeroGearCrypto.pbkdf2();
        byte[] salt = HMAC_STRING_SALT.getBytes();
        int iterations = 100000;
        SecretKey secretKey = pbkdf2.generateSecretKey(PASSWORD, salt, iterations);
        Hmac hmac = new Hmac("HmacSha512", secretKey);
        assertEquals(HMAC_STRING_DIGEST_SHA512, hmac.digest(HMAC_STRING_MESSAGE.getBytes()));
    }


    @Test(expected = RuntimeException.class)
    public void testAlgorithmNotFound() throws Exception {
        Pbkdf2 pbkdf2 = AeroGearCrypto.pbkdf2();
        byte[] salt = HMAC_STRING_SALT.getBytes();
        int iterations = 100000;
        SecretKey secretKey = pbkdf2.generateSecretKey(PASSWORD, salt, iterations);
        new Hmac("InvalidAlgorithm", secretKey);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidKey() throws Exception {
        SecretKey secretKey = null;
        new Hmac(secretKey);
    }

}
