package example;

import java.security.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class UseSHA3_2 {
  
  static String[] hashes = {"SHA3-224", "SHA3-256", "SHA3-384", "SHA3-512"};
  static MessageDigest md;

  public static void main(String[] a) {
    try {
      Security.addProvider(new BouncyCastleProvider());
      for (int i = 0; i < 4; i++) {
        md = MessageDigest.getInstance(hashes[i], "BC");
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
