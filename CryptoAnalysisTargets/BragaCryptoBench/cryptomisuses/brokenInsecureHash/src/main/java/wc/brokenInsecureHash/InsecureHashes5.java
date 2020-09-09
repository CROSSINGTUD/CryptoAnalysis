package wc.brokenInsecureHash;

import java.security.*;

public final class InsecureHashes5 {

  static MessageDigest md;

  public static void main(String[] a) {
    try {
      md = MessageDigest.getInstance("SHA","SUN");
      String input = "";
      md.update(input.getBytes());
      byte[] output = md.digest();
      input = "abc";
      md.update(input.getBytes());
      output = md.digest();
      input = "abcdefghijklmnopqrstuvwxyz";
      md.update(input.getBytes());
      output = md.digest();
    } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
    }
  }
}
