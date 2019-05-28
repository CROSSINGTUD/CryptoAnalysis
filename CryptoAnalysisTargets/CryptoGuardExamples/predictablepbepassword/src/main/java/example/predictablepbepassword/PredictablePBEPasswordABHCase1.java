package example.predictablepbepassword;

import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

public class PredictablePBEPasswordABHCase1 {
    private PBEKeySpec pbeKeySpec = null;
    private PBEParameterSpec pbeParamSpec = null;

    public static void main(String [] args) throws UnsupportedEncodingException {
        PredictablePBEPasswordABHCase1 ckp = new PredictablePBEPasswordABHCase1();
        SecureRandom random = new SecureRandom();
        String defaultKey = String.valueOf(random.ints());
        byte [] keyBytes = defaultKey.getBytes("UTF-8");
        ckp.key(keyBytes);
    }
    public void key(byte [] keyBytes) {
        byte [] salt = new byte[16];
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(salt);
        int iterationCount = 11010;
        int keyLength = 16;
        pbeKeySpec = new PBEKeySpec(new String(keyBytes).toCharArray(),salt,iterationCount,keyLength);
    }
}
