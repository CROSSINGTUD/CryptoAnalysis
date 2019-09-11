package example;

import static org.alexmbraga.utils.U.b2x;
import java.security.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class UseSHA3_1 {
  
  static MessageDigest md;

  public static void main(String[] a) {
    try {
      Security.addProvider(new BouncyCastleProvider()); // provedor BC
      
      md = MessageDigest.getInstance("SHA3-224", "BC");
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
    
      md = MessageDigest.getInstance("SHA3-256", "BC");
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
      
      md = MessageDigest.getInstance("SHA3-384", "BC");
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
      
      md = MessageDigest.getInstance("SHA3-512", "BC");
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
      
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }
  }
}
