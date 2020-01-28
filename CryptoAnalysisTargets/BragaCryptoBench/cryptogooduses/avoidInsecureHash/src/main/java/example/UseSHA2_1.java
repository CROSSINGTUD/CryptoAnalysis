package example;

import java.security.*;

public final class UseSHA2_1 {
  
  static String[] hashes = {"SHA-224", "SHA-256", "SHA-384", "SHA-512"};
  static MessageDigest md;

  public static void main(String[] a) {
    try {
      for (int i = 0; i < 4; i++) {
        md = MessageDigest.getInstance(hashes[i],"SUN");        
        String input = "";
        md.update(input.getBytes());
        byte[] output = md.digest();
        input = "abc";
        md.update(input.getBytes());
        output = md.digest();
        input = "abcdefghijklmnopqrstuvwxyz";
        md.update(input.getBytes());
        output = md.digest();
      }
      
    } catch (NoSuchAlgorithmException | NoSuchProviderException e) {}
  }
}
