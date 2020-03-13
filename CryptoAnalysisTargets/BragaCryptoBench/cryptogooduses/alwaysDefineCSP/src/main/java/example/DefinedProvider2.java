package example;

import java.security.KeyPairGenerator;
import java.security.Signature;

public final class DefinedProvider2 {

    public static void main(String[] args) throws Exception {

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC","SunEC");
        Signature signerA = Signature.getInstance("SHA512WithECDSA","SunEC");
        Signature verifierB = Signature.getInstance("SHA512WithECDSA","SunEC");
        
    }
}
