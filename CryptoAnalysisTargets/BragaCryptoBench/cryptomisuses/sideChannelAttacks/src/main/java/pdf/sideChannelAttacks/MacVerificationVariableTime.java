package pdf.sideChannelAttacks;

import _utils.U;
import java.security.*;
import java.util.Arrays;// this is insecure due to side channel
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;


public final class MacVerificationVariableTime {

  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException, 
          InvalidAlgorithmParameterException {

    KeyGenerator g = KeyGenerator.getInstance("HMACSHA256","SunJCE");
    g.init(256);
    Key k1 = g.generateKey();
    
    Mac m = Mac.getInstance("HMACSHA256","SunJCE");
    byte[] msgAna = "This is a test for MAC".getBytes();
    m.init(k1); 
    byte[] tag = m.doFinal(msgAna);
    
    //This is for verification 
    SecretKeySpec sks = new SecretKeySpec(k1.getEncoded(), "HMACSHA256");
    m.init(sks);
    byte[] tag2 = m.doFinal(msgAna);
    boolean ok = Arrays.equals(tag2,tag);//this is insecure due to side channel
    if (ok) {
      U.println("Do match!");
    } else {
      U.println("Do not match!");}
  }
  
}
