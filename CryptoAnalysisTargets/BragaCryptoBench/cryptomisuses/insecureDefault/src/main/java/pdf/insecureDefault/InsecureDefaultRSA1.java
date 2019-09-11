package pdf.insecureDefault;

import org.alexmbraga.utils.U;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public final class InsecureDefaultRSA1 {

  public static void main(String args[]) {
    try {
      //Security.addProvider(new BouncyCastleProvider()); // provedor BC
      byte[] msgAna = ("Insecure default RSA.").getBytes();
      KeyPairGenerator g = KeyPairGenerator.getInstance("RSA","SunMSCAPI");
      g.initialize(2048);
      KeyPair kp = g.generateKeyPair();

      U.println("Plaintext: " + new String(msgAna));

      Cipher enc = Cipher.getInstance("RSA","SunMSCAPI");
      enc.init(Cipher.ENCRYPT_MODE, kp.getPublic());
      Cipher dec = Cipher.getInstance("RSA","SunMSCAPI");
      dec.init(Cipher.DECRYPT_MODE, kp.getPrivate());

      U.println("Algorithm: " + enc.getAlgorithm());
      byte[][] ct = new byte[2][];
      for (int i = 0; i < 2; i++) {
        ct[i] = enc.doFinal(msgAna);
        byte[] ptBeto = dec.doFinal(ct[i]);
        U.println("Ciphertext: " + U.b2x(ct[i]));
      }

    } catch (NoSuchAlgorithmException | NoSuchPaddingException |
            InvalidKeyException | IllegalBlockSizeException |
            BadPaddingException | NoSuchProviderException  e) {
      System.out.println(e);
    }
  }
}
