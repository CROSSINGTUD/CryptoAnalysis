package example;

import example._utils.U;
import static example._utils.U.cancaoDoExilio;
import javax.crypto.*;
import java.security.*;

public final class CompareHashesInConstantTime1 {

  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException {

    //Security.addProvider(new BouncyCastleProvider()); // provedor BC

    MessageDigest md = MessageDigest.getInstance("SHA-512","SUN");
    boolean ok;
    long t1, t2;
    long t[] = new long[64];
    md.reset();
    byte[] hash1 = md.digest(cancaoDoExilio.getBytes());
    for (int j = 0; j < 1; j++) {

      for (int i = 0; i < t.length; i++) { // 64 bytes
        md.reset();
        byte[] hash2 = md.digest(cancaoDoExilio.getBytes());
        hash2[i] = (byte) (hash2[i] ^ 0x01);
        t1 = System.nanoTime();
        ok = MessageDigest.isEqual(hash2, hash1);
        t2 = System.nanoTime();
        t[i] = t2 - t1;
      }

      U.println("i;\t\tt[i];");
      for (int i = 0; i < t.length; i++) {
        U.println(i + ";\t\t" + t[i]);
      }
    }
  }
}
