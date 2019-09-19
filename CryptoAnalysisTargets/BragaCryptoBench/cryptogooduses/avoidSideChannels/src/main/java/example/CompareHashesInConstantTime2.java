
package example;

import org.alexmbraga.utils.U;
import static org.alexmbraga.utils.U.cancaoDoExilio;
import javax.crypto.*;
import java.security.*;
import org.bouncycastle.jce.provider.*;
import org.bouncycastle.util.Arrays;

public final class CompareHashesInConstantTime2 {

  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException {

    Security.addProvider(new BouncyCastleProvider()); // provedor BC

    MessageDigest md = MessageDigest.getInstance("SHA-512", "BC");
    boolean ok;
    long t1, t2;
    long t[] = new long[64], tt[] = new long[64];
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

      for (int i = 0; i < t.length; i++) { // 64 bytes
        md.reset();
        byte[] hash2 = md.digest(cancaoDoExilio.getBytes());
        hash2[i] = (byte) (hash2[i] ^ 0x01);
        t1 = System.nanoTime();
        ok = Arrays.constantTimeAreEqual(hash2, hash1);
        t2 = System.nanoTime();
        tt[i] = t2 - t1;
      }
    
      U.println("i;\t\tt[i];\t\ttt[i];");
      for (int i = 0; i < t.length; i++) {
        U.println(i + ";\t\t" + t[i] + ";\t\t" + tt[i]);
      }
    }
  }
}

