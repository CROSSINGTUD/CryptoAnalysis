package cai.undefinedCSP;

import java.security.*;
import javax.crypto.*;

public final class UndefinedProvider4 {

    public static void main(String argv[]) {
        try {
            KeyPairGenerator alice_kpg = KeyPairGenerator.getInstance("DH");
            System.out.println("KeyPairGen " + alice_kpg.getProvider().getName());

            KeyAgreement aliceka = KeyAgreement.getInstance("DH");
            System.out.println("Key Agreement " + aliceka.getProvider().getName());

            /* Let's turn over to Bob.
             * Bob has received Alice's public key in encoded format.
             * He instantiates a DH public key from the encoded key
             * material. */
            KeyFactory bobkf = KeyFactory.getInstance("DH");
            System.out.println("Key factory " + bobkf.getProvider().getName());

            // Bob creates his own DH key pair
            KeyPairGenerator bob_kpg = KeyPairGenerator.getInstance("DH");
            System.out.println("KeyPairGen " + bob_kpg.getProvider().getName());

            KeyAgreement bobka = KeyAgreement.getInstance("DH");
            System.out.println("KeyAgreement " + bobka.getProvider().getName());

            /* Alice uses Bob's public key for the first (and only)
             * phase of her version of the DH protocol.
             * Before she can do so, she has to instantiate a DH
             * public key from Bob's encoded key material. */
            KeyFactory alicekf = KeyFactory.getInstance("DH");
            System.out.println("KeyFactory " + alicekf.getProvider().getName());
        } catch (Exception e) {
            System.err.println("Error: " + e);
            System.exit(1);
        }
    }
}
