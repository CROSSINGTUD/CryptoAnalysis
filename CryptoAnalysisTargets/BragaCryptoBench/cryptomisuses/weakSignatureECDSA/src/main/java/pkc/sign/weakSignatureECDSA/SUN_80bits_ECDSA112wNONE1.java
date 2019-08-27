package pkc.sign.weakSignatureECDSA;

import _utils.U;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.Signature;

public final class SUN_80bits_ECDSA112wNONE1 {

    public static void main(String[] args) throws Exception {

        // par de chaves de Ana e configurações do criptosistema
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC","SunEC");
        kpg.initialize(112, new SecureRandom());
        Signature signerAna = Signature.getInstance("NONEwithECDSA","SunEC");

        KeyPair kpAna = kpg.generateKeyPair();

        //Ana assina o doc
        signerAna.initSign(kpAna.getPrivate(), new SecureRandom());
        byte[] doc = U.cancaoDoExilio.getBytes();
        MessageDigest md1 = MessageDigest.getInstance("SHA1","SUN");
        md1.update(doc);
        byte[] hash = md1.digest();
        signerAna.update(hash);
        byte[] assinatura = signerAna.sign();

        // Beto configura seu criptosistema
        Signature verifierBeto = Signature.getInstance("NONEwithECDSA","SunEC");

        MessageDigest md2 = MessageDigest.getInstance("SHA1","SUN");
        md2.update(doc);
        byte[] hash2 = md2.digest();
        //Beto verifica a assinatura
        verifierBeto.initVerify(kpAna.getPublic());
        verifierBeto.update(hash2);
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
