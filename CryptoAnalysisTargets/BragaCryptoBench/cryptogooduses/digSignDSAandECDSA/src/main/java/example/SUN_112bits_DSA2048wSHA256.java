package example;

import org.alexmbraga.utils.U;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;

public final class SUN_112bits_DSA2048wSHA256 {

    public static void main(String[] args) throws Exception {

        //Security.addProvider(new BouncyCastleProvider()); // provedor BC
 
        // par de chaves de Ana e configurações do criptosistema
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA","SUN");
        //System.out.println(kpg.getProvider().getName());
        kpg.initialize(2048, new SecureRandom());
        Signature signerAna = Signature.getInstance("SHA256WithDSA","SUN");

        KeyPair kpAna = kpg.generateKeyPair();

        //Ana assina o doc
        signerAna.initSign(kpAna.getPrivate(), new SecureRandom());
        byte[] doc = U.cancaoDoExilio.getBytes();
        signerAna.update(doc);
        byte[] assinatura = signerAna.sign();

        // Beto configura seu criptosistema
        Signature verifierBeto = Signature.getInstance("SHA256WithDSA","SUN");

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
