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
public final class UseOAEPForRSA {

  public static void main(String args[]) {
    try {
      Security.addProvider(new BouncyCastleProvider()); // provedor BC
      byte[] ptAna = ("Randomized RSA").getBytes();

      KeyPairGenerator g = KeyPairGenerator.getInstance("RSA", "BC");
      g.initialize(2048);
      KeyPair kp = g.generateKeyPair();
      U.println("Plaintext : " + new String(ptAna));
      //for (int a = 0; a < rsa.length; a++) {
      Cipher enc = Cipher.getInstance("RSA/None/OAEPWithSHA256AndMGF1Padding", "BC");
      enc.init(Cipher.ENCRYPT_MODE, kp.getPublic());
      Cipher dec = Cipher.getInstance("RSA/None/OAEPWithSHA256AndMGF1Padding", "BC");
      dec.init(Cipher.DECRYPT_MODE, kp.getPrivate());

      U.println("Algorithm: " + enc.getAlgorithm());
      byte[][] ct = new byte[2][];
      for (int i = 0; i < 2; i++) {
        ct[i] = enc.doFinal(ptAna);
        byte[] ptBeto = dec.doFinal(ct[i]);
        U.println("Ciphertext: " + U.b2x(ct[i]));
      }
    } catch (NoSuchAlgorithmException | NoSuchPaddingException |
            InvalidKeyException | IllegalBlockSizeException |
            BadPaddingException | NoSuchProviderException e) {
      System.out.println(e);
    }
  }
}
