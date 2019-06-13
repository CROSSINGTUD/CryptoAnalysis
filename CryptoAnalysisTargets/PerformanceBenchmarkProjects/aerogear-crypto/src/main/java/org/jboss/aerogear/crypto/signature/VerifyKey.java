package org.jboss.aerogear.crypto.signature;

import org.jboss.aerogear.crypto.encoders.Encoder;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import static org.jboss.aerogear.AeroGearCrypto.DEFAULT_ECDSA_SHA;

public class VerifyKey {

    private final Signature ecdsaSign;

    public VerifyKey(PublicKey publicKey) {
        try {
            this.ecdsaSign = Signature.getInstance(DEFAULT_ECDSA_SHA);
            ecdsaSign.initVerify(publicKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error: ", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Error: ", e);
        }
    }

    public boolean verify(byte[] message, byte[] signature) {

        boolean isValid;

        try {
            ecdsaSign.update(message);
            isValid = ecdsaSign.verify(signature);
        } catch (SignatureException e) {
            throw new RuntimeException("Corrupted message", e);
        }
        return isValid;
    }

    public Boolean verify(String message, String signature, Encoder encoder) {
        return verify(encoder.decode(message), encoder.decode(signature));
    }

    public Boolean verify(String message, String signature) throws SignatureException, InvalidKeyException {
        return verify(message, signature, Encoder.RAW);
    }

    public Boolean verify(String message, byte[] signature) throws SignatureException, InvalidKeyException {
        return verify(Encoder.RAW.decode(message), signature);
    }
}
