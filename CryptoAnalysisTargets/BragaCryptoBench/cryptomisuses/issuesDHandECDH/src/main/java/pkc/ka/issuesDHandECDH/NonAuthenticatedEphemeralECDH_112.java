package pkc.ka.issuesDHandECDH;

import _utils.U;
import java.security.*;
import java.security.spec.*;
import java.util.Arrays;
import javax.crypto.*;

public final class NonAuthenticatedEphemeralECDH_112 {

    public static void main(String argv[]) {
        try {
            
            //This is a misuse not only because of the small key, but also 
            // because it is not authenticated (it is not AECDH)

            U.println("ALICE: Generate DH keypair ...");
            KeyPairGenerator alice_kpg = KeyPairGenerator.getInstance("EC","SunEC");
            alice_kpg.initialize(224); // this has only 112 bits of security
            KeyPair alicekp = alice_kpg.generateKeyPair();
            //U.println("Alice pub  key: " + alicekp.getPublic());
            //U.println("Alice priv key: " + 
            //        U.b2x(alicekp.getPrivate().getEncoded()));
            // Alice creates and initializes her DH KeyAgreement object
            U.println("ALICE: Initialization ...");
            KeyAgreement aliceka = KeyAgreement.getInstance("ECDH","SunEC");
            aliceka.init(alicekp.getPrivate());

            // Alice encodes her public key, and sends it over to Bob.
            byte[] alicePubk = alicekp.getPublic().getEncoded();

            U.println("Bob's turn ...");
            /* Let's turn over to Bob.
             * Bob has received Alice's public key in encoded format.
             * He instantiates a DH public key from the encoded key
             * material. */
            KeyFactory bobkf = KeyFactory.getInstance("EC","SunEC");
            X509EncodedKeySpec x509ks = new X509EncodedKeySpec(alicePubk);
            PublicKey apk = bobkf.generatePublic(x509ks);

            /* Bob gets the DH parameters associated with Alice's
             * public key.
             * He must use the same parameters when he generates
             * his own key pair. */

            // Bob creates his own DH key pair
            System.out.println("BOB: Generate DH keypair ...");
            KeyPairGenerator bob_kpg = KeyPairGenerator.getInstance("EC","SunEC");
            bob_kpg.initialize(224);
            KeyPair bobkp = bob_kpg.generateKeyPair();
            //U.println("Bob pub  key: " + bobkp.getPublic());
            //U.println("Bob priv key: " + 
            //        U.b2x(bobkp.getPrivate().getEncoded()));
            // Bob creates and initializes his DH KeyAgreement object
            System.out.println("BOB: Initialization ...");
            KeyAgreement bobka = KeyAgreement.getInstance("ECDH","SunEC");
            bobka.init(bobkp.getPrivate());

            // Bob encodes his public key, and sends it over to Alice.
            byte[] bobPubk = bobkp.getPublic().getEncoded();

            /* Alice uses Bob's public key for the first (and only)
             * phase of her version of the DH protocol.
             * Before she can do so, she has to instantiate a DH
             * public key from Bob's encoded key material. */
            KeyFactory alicekf = KeyFactory.getInstance("EC","SunEC");
            x509ks = new X509EncodedKeySpec(bobPubk);
            PublicKey bobPubKey = alicekf.generatePublic(x509ks);
            System.out.println("ALICE: Execute PHASE1 ...");
            aliceka.doPhase(bobPubKey, true);
            byte[] alice_ss = aliceka.generateSecret();


            /* Bob uses Alice's public key for the first (and only)
             * phase of his version of the DH protocol. */
            U.println("BOB: Execute PHASE1 ...");
            bobka.doPhase(apk, true);
            byte[] bob_ss = bobka.generateSecret();

            /* At this stage, both Alice and Bob have completed
             * the DH key agreement protocol.
             * Both generate the (same) shared secret. */
            //System.out.println("Alice secret: " + U.b2x(alice_ss));
            //System.out.println("Bob   secret: " + U.b2x(bob_ss));

            if (!Arrays.equals(alice_ss, bob_ss)) {
                throw new Exception("Shared secrets differ");
            }
            U.println("Shared secrets are the same and are "
                    +bob_ss.length+" bytes long");        
        } catch (Exception e) {
            System.err.println("Error: " + e);
            System.exit(1);
        }
    }
}