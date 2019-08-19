package example;

import static example._utils.U.b2x;
import javax.crypto.*;
import java.security.*;
import javax.crypto.spec.*;

public final class UseHMACSHA2_1 {

  static String[] HMACs = {"HmacSHA224","HmacSHA256","HmacSHA384","HmacSHA512"}; 
  
  public static void main(String[] args) throws Exception {

      try {
        for (int i = 0; i < 4; i++) {
          KeyGenerator kg = KeyGenerator.getInstance(HMACs[i],"SunJCE");
          SecretKey sk = kg.generateKey();
          Mac mac = Mac.getInstance(HMACs[i],"SunJCE");
          mac.init(sk);
          String msg = "Minha terra tem palmeiras, onde canta o sabiá";
          mac.update(msg.getBytes());
          byte[] result = mac.doFinal();
          byte[] key2 = sk.getEncoded();
          System.out.println("\nAlgoritmo: " + mac.getAlgorithm());
          System.out.println("Tamanho : " + mac.getMacLength());
          //System.out.println("Chave: " + b2x(key2));
          System.out.println("MSG:   " + msg);
          System.out.println("MAC1:  " + b2x(result));
          SecretKeySpec ks = new SecretKeySpec(key2, HMACs[i]);
          Mac mac2 = Mac.getInstance(HMACs[i],"SunJCE");
          mac2.init(ks);
          mac2.update(msg.getBytes());
          byte[] result2 = mac2.doFinal();
          System.out.println("MAC2:  " + b2x(result2));
          //result[0] = (byte) (result[0] & 0x01);
          if (MessageDigest.isEqual(result, result2)) {
            System.out.println("MAC confere!");
          } else {
            System.out.println("MAC Não confere!");
          }
        }
        
      } catch (NoSuchAlgorithmException | 
              InvalidKeyException | 
              IllegalStateException e) 
      { System.out.println(e);}
  }
}
