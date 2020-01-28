package example;

import java.security.KeyPairGenerator;
import java.security.Signature;

public final class DefinedProvider1 {

    public static void main(String[] args) throws Exception {

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA","SUN");
        Signature signerA = Signature.getInstance("SHA256WithDSA","SUN");
        Signature verifierB = Signature.getInstance("SHA256WithDSA","SUN");
        
    }
}
