package org.jboss.aerogear.crypto;

import org.jboss.aerogear.crypto.encoders.Encoder;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.jboss.aerogear.AeroGearCrypto.HMAC_ALGORITHM;
import static org.jboss.aerogear.AeroGearCrypto.pbkdf2;

public class Hmac {

    private Mac hmac;

    public Hmac(String algorithm, SecretKey secretKey) {
        try {
            this.hmac = Mac.getInstance(algorithm);
            this.hmac.init(secretKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithm not supported: " + algorithm);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Invalid key: " + e.getCause());
        }
    }


    public Hmac(String algorithm, String secret) throws InvalidKeySpecException {
        this(algorithm, pbkdf2().generateSecretKey(secret));
    }

    public Hmac(String secret) throws InvalidKeySpecException {
        this(HMAC_ALGORITHM, secret);
    }
    
    public Hmac(SecretKey secretKey){
        this(HMAC_ALGORITHM, secretKey);
    }

    public String digest(Encoder encoder, byte[] message) throws NoSuchAlgorithmException {
        return encoder.encode(hmac.doFinal(message));
    }

    public String digest(byte[] message) throws NoSuchAlgorithmException {
        return this.digest(Encoder.BASE64, hmac.doFinal(message));
    }

    public String digest() throws NoSuchAlgorithmException {
        return this.digest(Encoder.BASE64, hmac.doFinal(RandomUtils.randomBytes()));
    }

}
