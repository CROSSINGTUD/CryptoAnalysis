package org.jboss.aerogear.crypto.encoders;

public class Base64 implements Encoder {

    @Override
    public byte[] decode(String data) {
        return org.bouncycastle.util.encoders.Base64.decode(data);
    }

    @Override
    public String encode(byte[] data) {
        return new String(org.bouncycastle.util.encoders.Base64.encode(data), CHARSET);
    }
}
