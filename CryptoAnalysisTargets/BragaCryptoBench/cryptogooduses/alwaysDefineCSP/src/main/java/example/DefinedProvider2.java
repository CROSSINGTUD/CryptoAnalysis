package example;

import java.security.KeyPairGenerator;
import java.security.Signature;

public final class DefinedProvider2 {

    public static void main(String[] args) throws Exception {

        // par de chaves de Ana e configurações do criptosistema
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC","SunEC");
        System.out.println("KeyPairGen "+kpg.getProvider().getName());
        
        Signature signerAna = Signature.getInstance("SHA512WithECDSA","SunEC");
        System.out.println("Signer "+signerAna.getProvider().getName());
        
        // Beto configura seu criptosistema
        Signature verifierBeto = Signature.getInstance("SHA512WithECDSA","SunEC");
        System.out.println("Verifier "+verifierBeto.getProvider().getName());
        
    }
}
