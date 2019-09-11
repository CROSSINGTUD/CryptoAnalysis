package example;

import org.alexmbraga.utils.U;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class BC_ECDSAprime239 {

    public static void main(String[] args) throws Exception {

        Security.addProvider(new BouncyCastleProvider()); // provedor BC

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("ECDSA", "BC");

        // par de chaves de Ana e configurações do criptosistema
        //curve prime 256|239|192 bits
        ECGenParameterSpec ec = new ECGenParameterSpec("prime239v1");
        kpg.initialize(ec, new SecureRandom());
        Signature signer = Signature.getInstance("SHA256WithECDSA", "BC");

        KeyPair kpAna = kpg.generateKeyPair();

        //Ana assina o doc
        signer.initSign(kpAna.getPrivate(), new SecureRandom());
        byte[] doc = U.cancaoDoExilio.getBytes();
        signer.update(doc);
        byte[] signature = signer.sign();

        Signature verifier = Signature.getInstance("SHA256WithECDSA", "BC");

        //Beto verifica a signature
        verifier.initVerify(kpAna.getPublic());
        verifier.update(doc);
        boolean ok = verifier.verify(signature);

        if (ok) {
            System.out.println("Signature OK!");
        } else {
            System.out.println("Signature not OK!");
        }

        //U.println("Public key " + kpAna.getPublic());
        //U.println("Private key "+ U.b2x(kpAna.getPrivate().getEncoded()));
        U.println("Algorithm: " + signer.getAlgorithm());
        U.println("Signature size: " + signature.length + " bytes");
        U.println("Signature: " + U.b2x(signature));
    }
}
