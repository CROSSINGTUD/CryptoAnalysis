package example;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public final class UseDynamicKeyForAES {

    public static void main(String[] a) {
        try {
            KeyGenerator g = KeyGenerator.getInstance("AES","SunJCE");
            g.init(128);
            Key k = g.generateKey();
            byte[] iv = new byte[16];
            SecureRandom.getInstanceStrong().nextBytes(iv);
            byte[] msg = "This is a test for AES..".getBytes();
            Cipher c = Cipher.getInstance("AES/CTR/NoPadding", "SunJCE");
            AlgorithmParameterSpec aps = new IvParameterSpec(iv);
            c.init(Cipher.ENCRYPT_MODE, k, aps);
            byte[] ct = c.doFinal(msg);
            
            SecretKeySpec ks1 = new SecretKeySpec(k.getEncoded(), "AES");
            c.init(Cipher.DECRYPT_MODE, ks1, aps);
            byte[] pt = c.doFinal(ct);
        } catch (InvalidKeyException | NoSuchAlgorithmException |
                NoSuchPaddingException | InvalidAlgorithmParameterException |
                IllegalBlockSizeException | BadPaddingException |
                NoSuchProviderException e) {}
    }
}
