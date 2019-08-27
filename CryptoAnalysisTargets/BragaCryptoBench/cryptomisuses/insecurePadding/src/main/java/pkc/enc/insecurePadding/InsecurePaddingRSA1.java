package pkc.enc.insecurePadding;

import _utils.U;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class InsecurePaddingRSA1 {

  public static void main(String args[]) {
    try {
      Security.addProvider(new BouncyCastleProvider()); // provedor BC
      byte[] msgAna = ("Cripto deterministica").getBytes();
      KeyPairGenerator g = KeyPairGenerator.getInstance("RSA", "BC");
      g.initialize(2048);
      KeyPair kp = g.generateKeyPair();

      U.println("Texto claro   : " + new String(msgAna));

      Cipher enc = Cipher.getInstance("RSA/ECB/NoPadding", "BC");
      enc.init(Cipher.ENCRYPT_MODE, kp.getPublic());
      Cipher dec = Cipher.getInstance("RSA/ECB/NoPadding", "BC");
      dec.init(Cipher.DECRYPT_MODE, kp.getPrivate());

      U.println("Encriptado com: " + enc.getAlgorithm());
      byte[][] ct = new byte[2][];
      for (int i = 0; i < 2; i++) {
        ct[i] = enc.doFinal(msgAna);
        byte[] textoClaroBeto = dec.doFinal(ct[i]);
        U.println("Criptograma   : " + U.b2x(ct[i]));
      }

    } catch (NoSuchAlgorithmException | NoSuchPaddingException |
            InvalidKeyException | IllegalBlockSizeException |
            BadPaddingException | NoSuchProviderException e) {
      System.out.println(e);
    }
  }
}
