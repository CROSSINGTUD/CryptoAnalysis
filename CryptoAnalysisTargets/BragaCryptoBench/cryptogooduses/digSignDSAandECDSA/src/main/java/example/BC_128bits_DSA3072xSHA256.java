package example;

import example._utils.U;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class BC_128bits_DSA3072xSHA256 {

    public static void main(String[] args) throws Exception {

        Security.addProvider(new BouncyCastleProvider()); // provedor BC
 
        // par de chaves de Ana e configurações do criptosistema
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA", "BC");
        kpg.initialize(3072, new SecureRandom()); // 3072 com SHA256
        Signature signerAna = Signature.getInstance("SHA256WithDSA", "BC");

        KeyPair kpAna = kpg.generateKeyPair();

        //Ana assina o doc
        signerAna.initSign(kpAna.getPrivate(), new SecureRandom());
        byte[] doc = U.cancaoDoExilio.getBytes();
        signerAna.update(doc);
        byte[] assinatura = signerAna.sign();

        // Beto configura seu criptosistema
        Signature verifierBeto = Signature.getInstance("SHA256WithDSA", "BC");

        //Beto verifica a assinatura
        verifierBeto.initVerify(kpAna.getPublic());
        verifierBeto.update(doc);
        boolean ok = verifierBeto.verify(assinatura);

        if (ok) {
            System.out.println("Signature OK!");
        } else {
            System.out.println("Signature not OK!");
        }

        //U.println("Public key " + kpAna.getPublic());
        //U.println("Private key "+ U.b2x(kpAna.getPrivate().getEncoded()));
        U.println("Algorithm: " + signerAna.getAlgorithm());
        U.println("Signature size: " + assinatura.length + " bytes");
        U.println("Signature: " + U.b2x(assinatura));
    }
}
