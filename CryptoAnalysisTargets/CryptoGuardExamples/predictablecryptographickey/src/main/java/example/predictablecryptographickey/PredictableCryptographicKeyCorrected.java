package example.predictablecryptographickey;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class PredictableCryptographicKeyCorrected {
    public static void main(String [] args) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException {
        SecureRandom random = new SecureRandom();
        String defaultKey = String.valueOf(random.ints());

        String originalString = "Testing";
        byte[] keyBytes = defaultKey.getBytes();
        keyBytes = Arrays.copyOf(keyBytes,16);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        String encrypt = Base64.getEncoder().encodeToString(cipher.doFinal(originalString.getBytes("UTF-8")));
        System.out.println(encrypt);

    }
}
