package cib.codingErrors;

import java.io.UnsupportedEncodingException;
import java.security.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

//explanations found in 
//www.whitehatsec.com/blog/hash-length-extension-attacks
//www.javacodegeeks.com/2012/07/hash-length-extension-attacks.html
public final class ConcatenatedHash2 {

  static MessageDigest md;

  static String i32 = "abcdefghijklmnopqrstuvxwyz012345";//32 bytes
  static String secret = i32;
  static String resource = i32;
  static String extension = i32 + i32;//64 bytes
 
  static boolean verify(String half1, String half2, byte[] hash)
          throws NoSuchAlgorithmException, NoSuchProviderException {
    Security.addProvider(new BouncyCastleProvider()); // provedor BC
    md = MessageDigest.getInstance("SHA-256", "BC");
   // this is the misuse
    byte[] h = md.digest((half1+half2).getBytes());
    return MessageDigest.isEqual(h, hash);
  }

  static boolean server(String half2, byte[] hash) {
    boolean ok = false;
    try {
      ok = verify(secret, half2, hash);
    } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
      System.out.println("Exception: " + e);
    }
    return ok;
  }

  public static void main(String[] a) {
    try {
      Security.addProvider(new BouncyCastleProvider()); // provedor BC
      md = MessageDigest.getInstance("SHA-256", "BC");
      byte[] h = md.digest((secret+resource).getBytes("UTF-8"));

      // legitimate client uses vulnerable code
      boolean ok = server(resource, h);
      System.out.println(ok);

      
    } catch (NoSuchAlgorithmException | NoSuchProviderException | 
            UnsupportedEncodingException e) {
      System.out.println("Exception: " + e);
    }
  }
}

 
