package example.predictablecryptographickey;

import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;

public class PredictableCryptographicKeyABICase2 {
    public static final String DEFAULT_ENCRYPT_KEY = "defaultkey";
    private static char[] ENCRYPT_KEY;
    private static char[] encryptKey;
    public static void main(String [] args){
        go2();
        go3();
        go();
    }

    private static void go2(){
        ENCRYPT_KEY = DEFAULT_ENCRYPT_KEY.toCharArray();
    }
    private static void go3(){
        encryptKey = ENCRYPT_KEY;
    }

    private static void go() {
        byte[] keyBytes = encryptKey.toString().getBytes();
        keyBytes = Arrays.copyOf(keyBytes,16);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
    }
}
