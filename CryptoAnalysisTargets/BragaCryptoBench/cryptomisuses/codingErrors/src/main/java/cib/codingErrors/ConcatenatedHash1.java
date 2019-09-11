package cib.codingErrors;

import static org.alexmbraga.utils.U.b2x;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

//explanations found in 
//www.whitehatsec.com/blog/hash-length-extension-attacks
//www.javacodegeeks.com/2012/07/hash-length-extension-attacks.html
public final class ConcatenatedHash1 {

  static MessageDigest md;

  static String i32 = "abcdefghijklmnopqrstuvxwyz012345";//32 bytes
  static String secret = i32;
  static String resource = i32;
  static String extension = i32 + i32;//64 bytes
  static byte[] padding;
  
  static{
    secret = "0123456789a";
    resource = "0123456789";
    padding = new byte[]{(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
                      (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
                      (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
                      (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
                      (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
                      (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
                      (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
                      (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
                      (byte)0x00,(byte)0x00,(byte)0xA8};
    extension = "TheResourceRemainsUnsecured"; 
  }

  static boolean verify(byte[] half1, byte[] half2, byte[] hash)
          throws NoSuchAlgorithmException, NoSuchProviderException {
    Security.addProvider(new BouncyCastleProvider()); // provedor BC
    md = MessageDigest.getInstance("SHA-1", "BC");
    byte[] h = md.digest(Arrays.concatenate(half1,half2));
    return MessageDigest.isEqual(h, hash);
  }

  static boolean server(byte[] half2, byte[] hash) {
    boolean ok = false;
    try {
      ok = verify(secret.getBytes("UTF-8"), half2, hash);
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }
    return ok;
  }

  public static void main(String[] a) {
    try {
      Security.addProvider(new BouncyCastleProvider()); // provedor BC
      md = MessageDigest.getInstance("SHA-1", "BC");
      byte[] h = md.digest((secret+resource).getBytes("UTF-8"));

      // legitimate client uses vulnerable code
      boolean ok = server(resource.getBytes("UTF-8"), h);
      System.out.println(ok);

      // attacker does not know secret.
      // claculates new hash by modifying algorithm internal state
      attack(h);
    } catch (NoSuchAlgorithmException | NoSuchProviderException | 
            UnsupportedEncodingException e) {
      System.out.println("Exception: " + e);
    }
  }

  static void attack(byte[] h) throws UnsupportedEncodingException {
    // test attackSHA1
    byte[] iv = Arrays.concatenate(i2ba(1732584193),i2ba(-271733879),
            i2ba(-1732584194),i2ba(271733878));
    iv = Arrays.concatenate(iv,i2ba(-1009589776));
    System.out.println("testing attackSHA1");
    System.out.println(b2x(attackerSHA1((secret+resource).getBytes("UTF-8"),iv)));
    System.out.println(b2x(h));

    // attacker does not know secret.
    // claculates new hash by modifying algorithm internal state
    System.out.println("attacking");
    byte[] resourceBytes = resource.getBytes("UTF-8");
    byte[] extBytes = extension.getBytes("UTF-8");
    byte[] attackerMsg = Arrays.concatenate(resourceBytes, padding, extBytes);
    byte[] attackedHash = attackerSHA1(attackerMsg, h);

    System.out.println(b2x(attackedHash));
    byte[] test = Arrays.concatenate(secret.getBytes("UTF-8"),attackerMsg);
    System.out.println(b2x(attackerSHA1(test,iv)));

    boolean ok = server(attackerMsg, attackedHash);
    System.out.println(ok);
  }
  
  static byte[] attackerSHA1(byte[] x, byte[] iv) {
    int a = b2i(Arrays.copyOfRange(iv, 0, 4));
    int b = b2i(Arrays.copyOfRange(iv, 4, 8));
    int c = b2i(Arrays.copyOfRange(iv, 8, 12));
    int d = b2i(Arrays.copyOfRange(iv, 12, 16));
    int e = b2i(Arrays.copyOfRange(iv, 16, 20));
    return SHA1(x, a, b, c, d, e);
  }

  static byte[] SHA1(byte[] x) {
    int a = 1732584193;
    int b = -271733879;
    int c = -1732584194;
    int d = 271733878;
    int e = -1009589776;
    return SHA1(x, a, b, c, d, e);
  }

  static byte[] SHA1(byte[] x, int a, int b, int c, int d, int e) {
    int[] blks = new int[(((x.length + 8) >> 6) + 1) * 16];
    int i;
    for (i = 0; i < x.length; i++) {
      blks[i >> 2] |= x[i] << (24 - (i % 4) * 8);
    }
    blks[i >> 2] |= 0x80 << (24 - (i % 4) * 8);
    blks[blks.length - 1] = x.length * 8;

    // calculate 160 bit SHA1 hash of the sequence of blocks
    int[] w = new int[80];
    for (i = 0; i < blks.length; i += 16) {
      int olda = a;
      int oldb = b;
      int oldc = c;
      int oldd = d;
      int olde = e;

      for (int j = 0; j < 80; j++) {
        w[j] = (j < 16) ? blks[i + j]
                : (rol(w[j - 3] ^ w[j - 8] ^ w[j - 14] ^ w[j - 16], 1));

        int t = rol(a, 5) + e + w[j]
                + ((j < 20) ? 1518500249 + ((b & c) | ((~b) & d))
                        : (j < 40) ? 1859775393 + (b ^ c ^ d)
                                : (j < 60) ? -1894007588 + ((b & c) | (b & d) | (c & d))
                                        : -899497514 + (b ^ c ^ d));
        e = d;
        d = c;
        c = rol(b, 30);
        b = a;
        a = t;
      }

      a = a + olda;
      b = b + oldb;
      c = c + oldc;
      d = d + oldd;
      e = e + olde;
    }
    byte[] aBB = i2ba(a);
    byte[] bBB = i2ba(b);
    byte[] cBB = i2ba(c);
    byte[] dBB = i2ba(d);
    byte[] eBB = i2ba(e);
    byte[] abcdBB = Arrays.concatenate(aBB, bBB, cBB, dBB);
    byte[] abcdeBB = Arrays.concatenate(abcdBB, eBB);

    return abcdeBB;
  }

  static final byte[] i2ba(int i) {
    return new byte[]{
      (byte) (i >>> 24),
      (byte) (i >>> 16),
      (byte) (i >>> 8),
      (byte) i};
  }
  
  static int b2i(byte[] bytes) {
        return (int) ((0xFF & bytes[0]) << 24
                | (0xFF & bytes[1]) << 16
                | (0xFF & bytes[2]) << 8
                | (0xFF & bytes[3]));
    }


  static final int ba2i(byte[] ba) {
    return (new BigInteger(ba)).intValue();
  }
  
  static int ba2ib(byte[] bytes) {
     return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
}

  //Bitwise rotate a 32-bit number to the left
  static int rol(int num, int cnt) {
    return (num << cnt) | (num >>> (32 - cnt));
  }

}
