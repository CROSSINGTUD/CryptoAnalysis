package example;

import java.security.*;
import javax.crypto.*;

public final class DefinedProvider4 {

    public static void main(String argv[]) {
        try {
            KeyPairGenerator kpg1 = KeyPairGenerator.getInstance("DH","SunJCE");
            KeyAgreement ka1 = KeyAgreement.getInstance("DH","SunJCE");
            KeyFactory kf1 = KeyFactory.getInstance("DH","SunJCE");
            
            KeyPairGenerator kpg2 = KeyPairGenerator.getInstance("DH","SunJCE");
            KeyAgreement ka2 = KeyAgreement.getInstance("DH","SunJCE");
            KeyFactory kf2 = KeyFactory.getInstance("DH","SunJCE");
            
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {}
    }
}
