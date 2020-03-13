
package example;

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
    byte[] msg = "This is a test for MAC".getBytes();
    m.init(k1); 
    byte[] tag = m.doFinal(msg);
    
    SecretKeySpec sks = new SecretKeySpec(k1.getEncoded(), "HMACSHA256");
    m.init(sks);
    byte[] tag2 = m.doFinal(msg);
    boolean ok = MessageDigest.isEqual(tag2,tag); 
  }
}
