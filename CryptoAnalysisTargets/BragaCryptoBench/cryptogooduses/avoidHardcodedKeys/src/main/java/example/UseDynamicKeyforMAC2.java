
package example;

import example._utils.U;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;


public final class UseDynamicKeyforMAC2 {

  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException, 
          InvalidAlgorithmParameterException {

    KeyGenerator g = KeyGenerator.getInstance("HMACSHA256", "SunJCE");
    g.init(256);
    Key k1 = g.generateKey();
    
    Mac m = Mac.getInstance("HMACSHA256", "SunJCE");
    byte[] msgAna = "This is a test for MAC".getBytes();
    m.init(k1); 
    byte[] tag = m.doFinal(msgAna);
    
    //This is for verification 
    SecretKeySpec sks = new SecretKeySpec(k1.getEncoded(), "HMACSHA256");
    m.init(sks);
    byte[] tag2 = m.doFinal(msgAna);
    boolean ok = MessageDigest.isEqual(tag2,tag); 
    if (ok) {
      U.println("Do match!");
    } else {
      U.println("Do not match!");}
  }
  
}
