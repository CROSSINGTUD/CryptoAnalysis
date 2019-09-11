package wc.brokenInsecureMAC;


import static org.alexmbraga.utils.U.b2x;
import javax.crypto.*;
import java.security.*;
import javax.crypto.spec.*;

public final class InsecureMAC1 {

  public static void main(String[] args) {

      try {
        KeyGenerator kg = KeyGenerator.getInstance("HMACMD5","SunJCE");
        SecretKey sk = kg.generateKey();
        Mac mac = Mac.getInstance("HMACMD5","SunJCE");
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
        SecretKeySpec ks = new SecretKeySpec(key2,"HMACMD5");
        Mac mac2 = Mac.getInstance("HMACMD5","SunJCE");
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
        
        
        kg = KeyGenerator.getInstance("HMACSHA1","SunJCE");
        sk = kg.generateKey();
        mac = Mac.getInstance("HMACSHA1","SunJCE");
        mac.init(sk);
        msg = "Minha terra tem palmeiras, onde canta o sabiá";
        mac.update(msg.getBytes());
        result = mac.doFinal();
        key2 = sk.getEncoded();
        System.out.println("\nAlgoritmo: " + mac.getAlgorithm());
        System.out.println("Tamanho : " + mac.getMacLength());
        //System.out.println("Chave: " + b2x(key2));
        System.out.println("MSG:   " + msg);
        System.out.println("MAC1:  " + b2x(result));
        ks = new SecretKeySpec(key2,"HMACSHA1");
        mac2 = Mac.getInstance("HMACSHA1","SunJCE");
        mac2.init(ks);
        mac2.update(msg.getBytes());
        result2 = mac2.doFinal();
        System.out.println("MAC2:  " + b2x(result2));
        //result[0] = (byte) (result[0] & 0x01);
        if (MessageDigest.isEqual(result, result2)) {
          System.out.println("MAC confere!");
        } else {
          System.out.println("MAC Não confere!");
        }
        
      } catch (NoSuchAlgorithmException | InvalidKeyException | 
              IllegalStateException | NoSuchProviderException e) 
      { System.out.println(e);} 
  }
}
