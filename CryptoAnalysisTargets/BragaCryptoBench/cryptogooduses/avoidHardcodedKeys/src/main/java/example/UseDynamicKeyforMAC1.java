
package example;

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
    byte[] msg = "This is a test for MAC".getBytes(), X;
    m.init(sks); 
    byte[] tag = m.doFinal(msg);
    
    boolean ok = MessageDigest.isEqual(m.doFinal(msg), tag); 
  }
}
