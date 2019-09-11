package pkm.constantKey;

import static org.alexmbraga.utils.U.x2b;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public final class ConstantKey3DES {

  public static void main(String[] a) {
    try {
      byte[] ck = x2b("0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF");
      byte[] iv = new byte[8];
      (new SecureRandom()).nextBytes(iv);
      byte[] msg = "Alexandre Melo Braga,123".getBytes();
      KeySpec ks = new DESedeKeySpec(ck);
      SecretKeyFactory kf = SecretKeyFactory.getInstance("DESede","SunJCE");
      SecretKey k = kf.generateSecret(ks);
      Cipher c = Cipher.getInstance("DESede/CTR/NoPadding","SunJCE");
      AlgorithmParameterSpec aps = new IvParameterSpec(iv);
      c.init(Cipher.ENCRYPT_MODE, k, aps);
      byte[] ct = c.doFinal(msg);
      c.init(Cipher.DECRYPT_MODE, k, aps);
      byte[] pt = c.doFinal(ct);
      System.out.println("Clear text : " + new String(pt));
    } catch (InvalidKeyException | NoSuchAlgorithmException |
            InvalidKeySpecException | NoSuchPaddingException |
            InvalidAlgorithmParameterException | IllegalBlockSizeException |
            BadPaddingException | NoSuchProviderException e) {
      System.out.println(e);
    } 
  }
}
