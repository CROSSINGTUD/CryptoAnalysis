package example;

import static example._utils.U.b2x;
import java.security.*;

public final class UseSHA2_2 {
  
  static MessageDigest md;

  public static void main(String[] a) {
    try {
      md = MessageDigest.getInstance("SHA-224","SUN");
      System.out.println("\nMessage digest object info: ");
      System.out.println(" Algorithm = " + md.getAlgorithm());
      System.out.println(" Digest length (bytes) = " + md.getDigestLength());
      String input = "";
      md.update(input.getBytes());
      byte[] output = md.digest();
      System.out.print("Hash (\"" + input + "\") = ");
      System.out.println(b2x(output));
      input = "abc";
      md.update(input.getBytes());
      output = md.digest();
      System.out.print("Hash (\"" + input + "\") = ");
      System.out.println(b2x(output));
      input = "abcdefghijklmnopqrstuvwxyz";
      md.update(input.getBytes());
      output = md.digest();
      System.out.print("Hash (\"" + input + "\") = ");
      System.out.println(b2x(output));
    
      md = MessageDigest.getInstance("SHA-256","SUN");
      System.out.println("\nMessage digest object info: ");
      System.out.println(" Algorithm = " + md.getAlgorithm());
      System.out.println(" Digest length (bytes) = " + md.getDigestLength());
      input = "";
      md.update(input.getBytes());
      output = md.digest();
      System.out.print("Hash (\"" + input + "\") = ");
      System.out.println(b2x(output));
      input = "abc";
      md.update(input.getBytes());
      output = md.digest();
      System.out.print("Hash (\"" + input + "\") = ");
      System.out.println(b2x(output));
      input = "abcdefghijklmnopqrstuvwxyz";
      md.update(input.getBytes());
      output = md.digest();
      System.out.print("Hash (\"" + input + "\") = ");
      System.out.println(b2x(output));
      
      md = MessageDigest.getInstance("SHA-384","SUN");
      System.out.println("\nMessage digest object info: ");
      System.out.println(" Algorithm = " + md.getAlgorithm());
      System.out.println(" Digest length (bytes) = " + md.getDigestLength());
      input = "";
      md.update(input.getBytes());
      output = md.digest();
      System.out.print("Hash (\"" + input + "\") = ");
      System.out.println(b2x(output));
      input = "abc";
      md.update(input.getBytes());
      output = md.digest();
      System.out.print("Hash (\"" + input + "\") = ");
      System.out.println(b2x(output));
      input = "abcdefghijklmnopqrstuvwxyz";
      md.update(input.getBytes());
      output = md.digest();
      System.out.print("Hash (\"" + input + "\") = ");
      System.out.println(b2x(output));
      
      md = MessageDigest.getInstance("SHA-512","SUN");
      System.out.println("\nMessage digest object info: ");
      System.out.println(" Algorithm = " + md.getAlgorithm());
      System.out.println(" Digest length (bytes) = " + md.getDigestLength());
      input = "";
      md.update(input.getBytes());
      output = md.digest();
      System.out.print("Hash (\"" + input + "\") = ");
      System.out.println(b2x(output));
      input = "abc";
      md.update(input.getBytes());
      output = md.digest();
      System.out.print("Hash (\"" + input + "\") = ");
      System.out.println(b2x(output));
      input = "abcdefghijklmnopqrstuvwxyz";
      md.update(input.getBytes());
      output = md.digest();
      System.out.print("Hash (\"" + input + "\") = ");
      System.out.println(b2x(output));
      
    } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
      System.out.println("Exception: " + e);
    }
  }
}
