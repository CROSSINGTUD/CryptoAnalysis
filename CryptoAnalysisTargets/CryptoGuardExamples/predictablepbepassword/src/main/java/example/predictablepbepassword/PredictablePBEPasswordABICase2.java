package example.predictablepbepassword;

import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.SecureRandom;

public class PredictablePBEPasswordABICase2 {
    private PBEKeySpec pbeKeySpec = null;
    private PBEParameterSpec pbeParamSpec = null;
    //public static final String DEFAULT_ENCRYPT_KEY = "sagar";
    public static String KEY = "sagar";
    public static char [] DEFAULT_ENCRYPT_KEY = KEY.toCharArray();
    private static char[] ENCRYPT_KEY;
    private static char[] encryptKey;

    public static void main(String [] args) {
        PredictablePBEPasswordABICase2 pksp = new PredictablePBEPasswordABICase2();
        go2();
        go3();
        pksp.go();
    }

    private static void go2(){
        ENCRYPT_KEY = DEFAULT_ENCRYPT_KEY;
    }
    private static void go3(){
        encryptKey = ENCRYPT_KEY;
    }

    private void go() {
        SecureRandom sr = new SecureRandom();
        byte [] salt = new byte[16];
        sr.nextBytes(salt);
        pbeKeySpec = new PBEKeySpec(encryptKey,salt,10000,16);
    }
}
