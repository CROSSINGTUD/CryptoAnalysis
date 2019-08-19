
package example;

import example._utils.U;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;


public final class UseDynamicKeyforMAC1 {

  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException, 
          InvalidAlgorithmParameterException {

    byte[] key = new byte[16];
    SecureRandom.getInstanceStrong().nextBytes(key);
    SecretKeySpec sks = new SecretKeySpec(key, "HMACSHA256");
    Mac m = Mac.getInstance("HMACSHA256","SunJCE");
    byte[] msgAna = "This is a test for MAC".getBytes(), X;
    m.init(sks); 
    byte[] tag = m.doFinal(msgAna);
    
    //this is for verification
    boolean ok = MessageDigest.isEqual(m.doFinal(msgAna), tag); 
    if (ok) {
      U.println("Do match!");
    } else {
      U.println("Do not match!");}
  }
  
}
