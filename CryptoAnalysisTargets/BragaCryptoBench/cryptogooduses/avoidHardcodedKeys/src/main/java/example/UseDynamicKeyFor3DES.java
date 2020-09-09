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

public final class UseDynamicKeyFor3DES {

    public static void main(String[] a) {
        try {
            KeyGenerator g = KeyGenerator.getInstance("DESede", "SunJCE");
            g.init(168);
            Key k = g.generateKey();
            byte[] iv = new byte[8];
            SecureRandom.getInstanceStrong().nextBytes(iv);
            byte[] msg = "This is a test for 3DES.".getBytes();
            Cipher c = Cipher.getInstance("DESede/CTR/NoPadding", "SunJCE");
            AlgorithmParameterSpec aps = new IvParameterSpec(iv);
            c.init(Cipher.ENCRYPT_MODE, k, aps);
            byte[] ct = c.doFinal(msg);
            
            SecretKeySpec ks1 = new SecretKeySpec(k.getEncoded(), "DESede");
            c.init(Cipher.DECRYPT_MODE, ks1, aps);
            byte[] pt = c.doFinal(ct);
        } catch (InvalidKeyException | NoSuchAlgorithmException |
                NoSuchPaddingException | InvalidAlgorithmParameterException |
                IllegalBlockSizeException | BadPaddingException |
                NoSuchProviderException  e) {}
    }
}
