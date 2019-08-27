package pkm.constantKey;

import static _utils.U.x2b;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public final class ConstantKeyAES2 {

  public static void main(String[] a) {
    try {
      byte[] ck = x2b("0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF");
      byte[] iv = new byte[16];
      (new SecureRandom()).nextBytes(iv);
      byte[] msg = "Alexandre Melo Braga,123".getBytes();
      SecretKeySpec ks = new SecretKeySpec(ck, "AES");
      Cipher c = Cipher.getInstance("AES/CTR/NoPadding","SunJCE");
      AlgorithmParameterSpec aps = new IvParameterSpec(iv);
      c.init(Cipher.ENCRYPT_MODE, ks, aps);
      byte[] ct = c.doFinal(msg);
      c.init(Cipher.DECRYPT_MODE, ks, aps);
      byte[] pt = c.doFinal(ct);
      System.out.println("Clear text : " + new String(pt));
    } catch (InvalidKeyException | NoSuchAlgorithmException |
             NoSuchPaddingException | InvalidAlgorithmParameterException |
             IllegalBlockSizeException | BadPaddingException|
             NoSuchProviderException e) {
      System.out.println(e);
    }
  }
}
