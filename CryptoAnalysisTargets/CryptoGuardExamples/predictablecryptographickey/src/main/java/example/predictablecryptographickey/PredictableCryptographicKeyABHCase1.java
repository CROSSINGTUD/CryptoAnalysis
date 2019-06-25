package example.predictablecryptographickey;

import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Arrays;

public class PredictableCryptographicKeyABHCase1 {
    public static void main(String [] args) throws UnsupportedEncodingException {
        SecureRandom random = new SecureRandom();
        String defaultKey = String.valueOf(random.ints());

        byte [] keyBytes = defaultKey.getBytes("UTF-8");
        keyBytes = Arrays.copyOf(keyBytes,16);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
    }
}
