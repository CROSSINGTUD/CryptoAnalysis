package example.staticsalts;

import javax.crypto.spec.PBEParameterSpec;
import java.security.SecureRandom;

public class StaticSaltsCorrected {
    public static void main(String [] args){
        StaticSaltsCorrected cs = new StaticSaltsCorrected();
        cs.key2();
    }
    public void key2(){
        SecureRandom random = new SecureRandom();
        PBEParameterSpec pbeParamSpec = null;
        byte[] salt = new byte[32];
        random.nextBytes(salt);
        int count = 1020;
        pbeParamSpec = new PBEParameterSpec(salt, count);
    }
}
