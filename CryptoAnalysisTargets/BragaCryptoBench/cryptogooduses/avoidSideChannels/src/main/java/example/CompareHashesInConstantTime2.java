
package example;

import javax.crypto.*;
import java.security.*;
import org.bouncycastle.jce.provider.*;
import org.bouncycastle.util.Arrays;

public final class CompareHashesInConstantTime2 {

  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException {

    Security.addProvider(new BouncyCastleProvider());

    MessageDigest md = MessageDigest.getInstance("SHA-512", "BC");
    boolean ok;
    long t1, t2;
    long t[] = new long[64], tt[] = new long[64];
    md.reset();
    byte[] hash1 = md.digest("demo text".getBytes());
    for (int j = 0; j < 1; j++) {

      for (int i = 0; i < t.length; i++) {
        md.reset();
        byte[] hash2 = md.digest("demo text".getBytes());
        hash2[i] = (byte) (hash2[i] ^ 0x01);
        t1 = System.nanoTime();
        ok = MessageDigest.isEqual(hash2, hash1);
        t2 = System.nanoTime();
        t[i] = t2 - t1;
      }

      for (int i = 0; i < t.length; i++) {
        md.reset();
        byte[] hash2 = md.digest("demo text".getBytes());
        hash2[i] = (byte) (hash2[i] ^ 0x01);
        t1 = System.nanoTime();
        ok = Arrays.constantTimeAreEqual(hash2, hash1);
        t2 = System.nanoTime();
        tt[i] = t2 - t1;
      }
    }
  }
}

