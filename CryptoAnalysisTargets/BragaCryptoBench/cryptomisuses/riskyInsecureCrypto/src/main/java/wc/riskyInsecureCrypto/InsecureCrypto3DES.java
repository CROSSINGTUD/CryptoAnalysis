package wc.riskyInsecureCrypto;

import static org.alexmbraga.utils.U.b2x;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.SecureRandom;

public final class InsecureCrypto3DES {

  public static void main(String[] a) {

    try {
      byte[] msg = "Alexandre Melo Braga,123".getBytes();
      byte[] iv = new byte[8];
      (new SecureRandom()).nextBytes(iv);
      KeyGenerator kg = KeyGenerator.getInstance("DESede","SunJCE");
      kg.init(168);
      Key k = kg.generateKey();
      Cipher c = Cipher.getInstance("DESede/CTR/NoPadding","SunJCE");
      AlgorithmParameterSpec aps = new IvParameterSpec(iv);
      c.init(Cipher.ENCRYPT_MODE, k, aps);
      byte[] ct = c.doFinal(msg);
      iv = c.getIV();
      c.init(Cipher.DECRYPT_MODE, k, aps);
      byte[] pt = c.doFinal(ct);
      System.out.println("\nTeste 2: " + c.getAlgorithm());
      System.out.println("Bloco : " + c.getBlockSize());
      //System.out.println("Key : " + b2x(k.getEncoded()));
      System.out.println("IV  : " + b2x(iv));
      System.out.println("Message    : " + new String(msg));
      System.out.println("Msg (bytes): " + b2x(msg));
      System.out.println("Cipher     : " + b2x(ct));
      System.out.println("Clr (bytes): " + b2x(pt));
      System.out.println("Clear text : " + new String(pt));
    } catch (NoSuchAlgorithmException | NoSuchProviderException | 
            NoSuchPaddingException | InvalidKeyException | 
            InvalidAlgorithmParameterException | IllegalBlockSizeException | 
            BadPaddingException e) {
      System.out.println(e);
    }
  }
}
