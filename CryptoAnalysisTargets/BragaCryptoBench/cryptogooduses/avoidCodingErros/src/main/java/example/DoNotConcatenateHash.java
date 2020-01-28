package example;

import java.io.UnsupportedEncodingException;
import java.security.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class DoNotConcatenateHash {
  
  static MessageDigest md;

  static String i32 = "abcdefghijklmnopqrstuvxwyz012345";
  static String secret = i32;
  static String resource = i32;
  static String extension = i32 + i32;
 
  static boolean verify(byte[] half1, byte[] half2, byte[] hash)
          throws NoSuchAlgorithmException, NoSuchProviderException {
    Security.addProvider(new BouncyCastleProvider());
    md = MessageDigest.getInstance("SHA-256", "BC");
    md.update(half1);
    md.update(half2);
    byte[] h = md.digest();
    return MessageDigest.isEqual(h, hash);
  }

  static boolean server(byte[] half2, byte[] hash) {
    boolean ok = false;
    try {
      ok = verify(secret.getBytes("UTF-8"), half2, hash);
    } catch (UnsupportedEncodingException | NoSuchAlgorithmException | 
            NoSuchProviderException e) {}
    return ok;
  }

  public static void main(String[] a) {
    try {
      Security.addProvider(new BouncyCastleProvider());
      md = MessageDigest.getInstance("SHA-256", "BC");
      byte[] h = md.digest((secret+resource).getBytes("UTF-8"));

      boolean ok = server(resource.getBytes("UTF-8"), h);
      
    } catch (NoSuchAlgorithmException | NoSuchProviderException | 
            UnsupportedEncodingException e) {}
  }
}