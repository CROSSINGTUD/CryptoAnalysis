package example;

import java.security.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class UseSHA3_1 {
  
  static MessageDigest md;

  public static void main(String[] a) {
    try {
      Security.addProvider(new BouncyCastleProvider());
      
      md = MessageDigest.getInstance("SHA3-224", "BC");
      String input = "";
      md.update(input.getBytes());
      byte[] output = md.digest();
      input = "abc";
      md.update(input.getBytes());
      output = md.digest();
      input = "abcdefghijklmnopqrstuvwxyz";
      md.update(input.getBytes());
      output = md.digest();
    
      md = MessageDigest.getInstance("SHA3-256", "BC");
      input = "";
      md.update(input.getBytes());
      output = md.digest();
      input = "abc";
      md.update(input.getBytes());
      output = md.digest();
      input = "abcdefghijklmnopqrstuvwxyz";
      md.update(input.getBytes());
      output = md.digest();
      
      md = MessageDigest.getInstance("SHA3-384", "BC");
      input = "";
      md.update(input.getBytes());
      output = md.digest();
      input = "abc";
      md.update(input.getBytes());
      output = md.digest();
      input = "abcdefghijklmnopqrstuvwxyz";
      md.update(input.getBytes());
      output = md.digest();
      
      md = MessageDigest.getInstance("SHA3-512", "BC");
      input = "";
      md.update(input.getBytes());
      output = md.digest();
      input = "abc";
      md.update(input.getBytes());
      output = md.digest();
      input = "abcdefghijklmnopqrstuvwxyz";
      md.update(input.getBytes());
      output = md.digest();
      
    } catch (Exception e) {}
  }
}
