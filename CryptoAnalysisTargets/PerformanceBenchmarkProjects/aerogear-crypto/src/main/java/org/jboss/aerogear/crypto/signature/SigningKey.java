package org.jboss.aerogear.crypto.signature;

import org.jboss.aerogear.crypto.encoders.Encoder;
import org.jboss.aerogear.crypto.keys.KeyPair;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import static org.jboss.aerogear.AeroGearCrypto.DEFAULT_CURVE_NAME;
import static org.jboss.aerogear.AeroGearCrypto.DEFAULT_ECDSA_SHA;
import static org.jboss.aerogear.AeroGearCrypto.ECDSA_ALGORITHM_NAME;

public class SigningKey {

    private final PublicKey publicKey;

    private Signature ecdsaSign;

    public SigningKey(KeyPair keyPair) {
        this.publicKey = keyPair.getPublicKey();
        try {
            this.ecdsaSign = Signature.getInstance(DEFAULT_ECDSA_SHA);
            ecdsaSign.initSign(keyPair.getPrivateKey());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error: ", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Error: ", e);
        }
    }

    public SigningKey() {
        this(new KeyPair(ECDSA_ALGORITHM_NAME, DEFAULT_CURVE_NAME));
    }

    public byte[] sign(byte[] message) {

        byte[] signature;

        try {
            ecdsaSign.update(message);
            signature = ecdsaSign.sign();
        } catch (SignatureException e) {
            throw new RuntimeException("Error: ", e);
        }

        return signature;

    }

    public String sign(String message, Encoder encoder) {
        byte[] signature = sign(encoder.decode(message));
        return encoder.encode(signature);
    }


    public String sign(String message) {
        return sign(message, Encoder.RAW);
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}


