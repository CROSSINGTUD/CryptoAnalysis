package example;

import example._utils.U;
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

//1.4.2. Criptografia determinística assimétrica
public final class UsePKCS1ForRSA {

  public static void main(String args[]) {
    try {
      Security.addProvider(new BouncyCastleProvider()); // provedor BC
      byte[] ptAna = ("Randomized RSA").getBytes();
      KeyPairGenerator g = KeyPairGenerator.getInstance("RSA", "BC");
      g.initialize(2048);
      KeyPair kp = g.generateKeyPair();

      U.println("Plaintext: " + new String(ptAna));

      Cipher enc = Cipher.getInstance("RSA/None/PKCS1Padding", "BC");
      enc.init(Cipher.ENCRYPT_MODE, kp.getPublic());
      Cipher dec = Cipher.getInstance("RSA/None/PKCS1Padding", "BC");
      dec.init(Cipher.DECRYPT_MODE, kp.getPrivate());

      U.println("Algorithm : " + enc.getAlgorithm());
      byte[][] criptograma = new byte[2][];
      for (int i = 0; i < 2; i++) {
        criptograma[i] = enc.doFinal(ptAna);
        byte[] ptBeto = dec.doFinal(criptograma[i]);
        U.println("Ciphertext  : " + U.b2x(criptograma[i]));

      }

    } catch (NoSuchAlgorithmException | NoSuchPaddingException |
            InvalidKeyException | IllegalBlockSizeException |
            BadPaddingException | NoSuchProviderException e) {
      System.out.println(e);
    }
  }
}
