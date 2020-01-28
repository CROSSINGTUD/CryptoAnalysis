package example;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class BC_ECDSAprime256 {

    public static void main(String[] args) throws Exception {

        Security.addProvider(new BouncyCastleProvider());

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("ECDSA", "BC");

        ECGenParameterSpec ec = new ECGenParameterSpec("prime256v1");
        kpg.initialize(ec, new SecureRandom());
        Signature signer = Signature.getInstance("SHA256WithECDSA", "BC");

        KeyPair kp = kpg.generateKeyPair();

        signer.initSign(kp.getPrivate(), new SecureRandom());
        byte[] doc = "this is a demo text".getBytes();
        signer.update(doc);
        byte[] signature = signer.sign();

        Signature verifier = Signature.getInstance("SHA256WithECDSA", "BC");

        verifier.initVerify(kp.getPublic());
        verifier.update(doc);
        boolean ok = verifier.verify(signature);

    }
}
