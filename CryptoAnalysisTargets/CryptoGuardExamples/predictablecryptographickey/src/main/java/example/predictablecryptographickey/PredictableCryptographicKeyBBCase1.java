package example.predictablecryptographickey;

import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;

public class PredictableCryptographicKeyBBCase1 {
    public static void main(String [] args){
        String defaultKey = "defaultkey";
        byte[] keyBytes = defaultKey.getBytes();
        keyBytes = Arrays.copyOf(keyBytes,16);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
    }
}
