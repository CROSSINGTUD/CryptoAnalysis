package wc.brokenInsecureHash;

import static _utils.U.b2x;
import java.security.*;
import java.util.*;

public final class InsecureHashes2 {

  static Object[] hashes = {"MD2","MD5","SHA"};
  static MessageDigest md[] = new MessageDigest[hashes.length];

  public static void main(String[] a) {
    try {
      System.out.println("Hashs " + Arrays.toString(hashes));
      for (int i = 0; i < md.length; i++) {
        md[i] = MessageDigest.getInstance(hashes[i].toString(),"SUN");
        System.out.println("\nMessage digest object info: ");
        System.out.println(" Algorithm = " + md[i].getAlgorithm());
        System.out.println(" Digest length (bytes) = " + md[i].getDigestLength());
        String input = "";
        md[i].update(input.getBytes());
        byte[] output = md[i].digest();
        System.out.print("Hash (\"" + input + "\") = ");
        System.out.println(b2x(output));
        input = "abc";
        md[i].update(input.getBytes());
        output = md[i].digest();
        System.out.print("Hash (\"" + input + "\") = ");
        System.out.println(b2x(output));
        input = "abcdefghijklmnopqrstuvwxyz";
        md[i].update(input.getBytes());
        output = md[i].digest();
        System.out.print("Hash (\"" + input + "\") = ");
        System.out.println(b2x(output));
      }
    } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
      System.out.println("Exception: " + e);
    }
  }
}
