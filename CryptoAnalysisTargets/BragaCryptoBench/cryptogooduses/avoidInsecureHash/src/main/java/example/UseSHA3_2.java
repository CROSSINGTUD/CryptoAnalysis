package example;

import static example._utils.U.b2x;
import java.security.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class UseSHA3_2 {
  
  static String[] hashes = {"SHA3-224", "SHA3-256", "SHA3-384", "SHA3-512"};
  static MessageDigest md;

  public static void main(String[] a) {
    try {
      Security.addProvider(new BouncyCastleProvider()); // provedor BC
      for (int i = 0; i < 4; i++) {
        md = MessageDigest.getInstance(hashes[i],"BC");
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
      }
      
    } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
      System.out.println("Exception: " + e);
    }
  }
}
