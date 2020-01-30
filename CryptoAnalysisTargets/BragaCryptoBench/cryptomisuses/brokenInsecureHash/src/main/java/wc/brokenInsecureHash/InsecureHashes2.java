package wc.brokenInsecureHash;

import java.security.*;

public final class InsecureHashes2 {

  static Object[] hashes = {"MD2","MD5","SHA"};
  static MessageDigest md[] = new MessageDigest[hashes.length];

  public static void main(String[] a) {
    try {
      for (int i = 0; i < md.length; i++) {
        md[i] = MessageDigest.getInstance(hashes[i].toString(),"SUN");
        String input = "";
        md[i].update(input.getBytes());
        byte[] output = md[i].digest();
        input = "abc";
        md[i].update(input.getBytes());
        output = md[i].digest();
        input = "abcdefghijklmnopqrstuvwxyz";
        md[i].update(input.getBytes());
        output = md[i].digest();
      }
    } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
    }
  }
}
