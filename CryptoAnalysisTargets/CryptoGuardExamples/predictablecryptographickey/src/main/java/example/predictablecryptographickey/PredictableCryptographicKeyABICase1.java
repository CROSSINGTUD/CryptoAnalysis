package example.predictablecryptographickey;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

public class PredictableCryptographicKeyABICase1 {
    public static void main(String [] args){
        String key = "defaultkey";
        go(key);
    }

    private static void go(String key) {
        byte[] keyBytes = key.getBytes();
        keyBytes = Arrays.copyOf(keyBytes,16);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
    }
}
