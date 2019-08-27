package wc.customCrypto;

import static _utils.U.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.*;
import javax.crypto.spec.*;

public final class Manual3DES {

  public static void main(String[] a) {

    try {
      //String engine = ;
      String k1 = "0123456789ABCDEF";
      String k2 = "1123456789ABCDEF";
      String k3 = "2123456789ABCDEF";
      byte[] k123 = x2b(k1 + k1 + k1);// k1==k2==k3 (64 bits)
      //byte[] k123 = x2b(k1+k2+k1);// k1==k3!=k2 (128 bits)
      //byte[] k123 = x2b(k1+k2+k3);// k1!=k2!=k3 (192 bits)
      byte[] iv = null;
      byte[] msg = "Alexandre Melo Braga,123".getBytes();
      //String alg = engine + "/ECB/NoPadding";
      KeySpec ks = new DESedeKeySpec(k123);
      SecretKeyFactory kf = SecretKeyFactory.getInstance("DESede","SunJCE");
      SecretKey k = kf.generateSecret(ks);
      Cipher c = Cipher.getInstance("DESede","SunJCE");
      c.init(Cipher.ENCRYPT_MODE, k);
      byte[] theCph = c.doFinal(msg);
      c.init(Cipher.DECRYPT_MODE, k);
      byte[] theClear = c.doFinal(theCph);
      System.out.println("\nTeste 3DES EDE: " + c.getAlgorithm());
      System.out.println("Bloco : " + c.getBlockSize());
      //System.out.println("Key : " + b2x(k.getEncoded()));
      System.out.println("IV  : " + b2x(iv));
      System.out.println("Message    : " + new String(msg));
      System.out.println("Msg (bytes): " + b2x(msg));
      System.out.println("Cipher     : " + b2x(theCph));
      System.out.println("Clr (bytes): " + b2x(theClear));
      System.out.println("Clear text : " + new String(theClear));
    } catch (InvalidKeyException | NoSuchAlgorithmException | 
            InvalidKeySpecException | NoSuchPaddingException | 
            IllegalBlockSizeException | BadPaddingException|
            NoSuchProviderException e) {
      System.out.println(e);
    } 
  }

}
