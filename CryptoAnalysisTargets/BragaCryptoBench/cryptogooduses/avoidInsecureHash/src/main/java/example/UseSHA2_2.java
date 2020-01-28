package example;

import java.security.*;

public final class UseSHA2_2 {
  
  static MessageDigest md;

  public static void main(String[] a) {
    try {
      md = MessageDigest.getInstance("SHA-224","SUN");
      String input = "";
      md.update(input.getBytes());
      byte[] output = md.digest();
      input = "abc";
      md.update(input.getBytes());
      output = md.digest();
      input = "abcdefghijklmnopqrstuvwxyz";
      md.update(input.getBytes());
      output = md.digest();
    
      md = MessageDigest.getInstance("SHA-256","SUN");
      input = "";
      md.update(input.getBytes());
      output = md.digest();
      input = "abc";
      md.update(input.getBytes());
      output = md.digest();
      input = "abcdefghijklmnopqrstuvwxyz";
      md.update(input.getBytes());
      output = md.digest();
      
      md = MessageDigest.getInstance("SHA-384","SUN");
      input = "";
      md.update(input.getBytes());
      output = md.digest();
      input = "abc";
      md.update(input.getBytes());
      output = md.digest();
      input = "abcdefghijklmnopqrstuvwxyz";
      md.update(input.getBytes());
      output = md.digest();
      
      md = MessageDigest.getInstance("SHA-512","SUN");
      input = "";
      md.update(input.getBytes());
      output = md.digest();
      input = "abc";
      md.update(input.getBytes());
      output = md.digest();
      input = "abcdefghijklmnopqrstuvwxyz";
      md.update(input.getBytes());
      output = md.digest();
      
    } catch (NoSuchAlgorithmException | NoSuchProviderException e) {}
  }
}
