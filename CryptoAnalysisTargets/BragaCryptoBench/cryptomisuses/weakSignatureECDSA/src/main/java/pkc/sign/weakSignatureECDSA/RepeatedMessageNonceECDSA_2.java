package pkc.sign.weakSignatureECDSA;

import org.alexmbraga.utils.U;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;
import org.bouncycastle.util.Arrays;

/*
ECDSA signatures are randomized: each signature consists of two values (r; s): 
the value r is derived from an ephemeral public key kG generated using a random 
per-message nonce k, and a signature value s that depends on k. It is essential 
for the security of ECDSA that signers use unpredictable and distinct values for
k for every signature, since predictable or repeated values allow an adversary 
to eciently compute the long-term private key from one or two signature values.
*/

public final class RepeatedMessageNonceECDSA_2 {

    public static void main(String[] args) throws Exception {

        // par de chaves de Ana e configurações do criptosistema
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC","SunEC");
        kpg.initialize(256, SecureRandom.getInstanceStrong());
        
        KeyPair kpAna = kpg.generateKeyPair();

        byte[] seed = {0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09, 
                       0x0A,0x0B,0x0C,0x0D,0x0E,0x0F};
        
        SecureRandom sr1 = SecureRandom.getInstance("SHA1PRNG","SUN");        
        sr1.setSeed(seed);
        
        Signature signer1 = Signature.getInstance("SHA256withECDSA","SunEC");
        signer1.initSign(kpAna.getPrivate(), sr1);
        byte[] doc = U.cancaoDoExilio.getBytes();
        signer1.update(doc);
        byte[] sign1 = signer1.sign();
        
        SecureRandom sr2 = SecureRandom.getInstance("SHA1PRNG","SUN");
        sr2.setSeed(seed);
        
        Signature signer2 = Signature.getInstance("SHA256withECDSA","SunEC");
        signer2.initSign(kpAna.getPrivate(), sr2);
        doc = U.cancaoDoExilio.getBytes();
        signer2.update(doc);
        byte[] sign2 = signer2.sign();

        boolean ok = Arrays.areEqual(sign1, sign2);
        if (ok) {
            System.out.println("Nonce repeated! Signatures are equal!");
        } else {
            System.out.println("Signatures are different!");
        }
    }
}
