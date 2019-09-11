package pkc.sign.weakSignatureECDSA;

import org.alexmbraga.utils.U;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;

public final class SUN_80bits_ECDSA112wSHA1 {

    public static void main(String[] args) throws Exception {

        // par de chaves de Ana e configurações do criptosistema
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC","SunEC");
        kpg.initialize(112, new SecureRandom());
        Signature signerAna = Signature.getInstance("SHA1WithECDSA","SunEC");

        KeyPair kpAna = kpg.generateKeyPair();

        //Ana assina o doc
        signerAna.initSign(kpAna.getPrivate(), new SecureRandom());
        byte[] doc = U.cancaoDoExilio.getBytes();
        signerAna.update(doc);
        byte[] assinatura = signerAna.sign();

        // Beto configura seu criptosistema
        Signature verifierBeto = Signature.getInstance("SHA1WithECDSA","SunEC");

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
