package example;

import javax.crypto.*;
import java.security.*;
import javax.crypto.spec.*;

public final class UseHMACSHA2_1 {

  static String[] HMACs = {"HmacSHA224","HmacSHA256","HmacSHA384","HmacSHA512"}; 
  
  public static void main(String[] args) throws Exception {

      try {
        for (int i = 0; i < 4; i++) {
          KeyGenerator kg = KeyGenerator.getInstance(HMACs[i], "SunJCE");
          SecretKey sk = kg.generateKey();
          Mac mac = Mac.getInstance(HMACs[i], "SunJCE");
          mac.init(sk);
          String msg = "This is a demo msg";
          mac.update(msg.getBytes());
          byte[] result = mac.doFinal();
          byte[] key2 = sk.getEncoded();
          SecretKeySpec ks = new SecretKeySpec(key2, HMACs[i]);
          Mac mac2 = Mac.getInstance(HMACs[i], "SunJCE");
          mac2.init(ks);
          mac2.update(msg.getBytes());
          byte[] result2 = mac2.doFinal();
 
        }
        
      } catch (NoSuchAlgorithmException | 
              InvalidKeyException | 
              IllegalStateException e) {}
  }
}
