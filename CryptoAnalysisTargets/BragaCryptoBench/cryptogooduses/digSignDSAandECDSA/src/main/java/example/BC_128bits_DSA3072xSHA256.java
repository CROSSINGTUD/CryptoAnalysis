package example;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class BC_128bits_DSA3072xSHA256 {

    public static void main(String[] args) throws Exception {

        Security.addProvider(new BouncyCastleProvider());
 
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA", "BC");
        kpg.initialize(3072, new SecureRandom());
        Signature sign1 = Signature.getInstance("SHA256WithDSA", "BC");

        KeyPair kp1 = kpg.generateKeyPair();

        sign1.initSign(kp1.getPrivate(), new SecureRandom());
        byte[] doc = "this is a demo text".getBytes();
        sign1.update(doc);
        byte[] signed1 = sign1.sign();

        Signature verfier1 = Signature.getInstance("SHA256WithDSA", "BC");

        verfier1.initVerify(kp1.getPublic());
        verfier1.update(doc);
        boolean ok = verfier1.verify(signed1);
    }
}
