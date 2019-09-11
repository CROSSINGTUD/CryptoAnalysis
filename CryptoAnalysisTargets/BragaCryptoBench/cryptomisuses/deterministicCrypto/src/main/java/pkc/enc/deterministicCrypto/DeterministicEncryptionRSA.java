package pkc.enc.deterministicCrypto;

import org.alexmbraga.utils.U;
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


public final class DeterministicEncryptionRSA {

  public static void main(String args[]) {
    try {
      Security.addProvider(new BouncyCastleProvider()); // provedor BC
      byte[] textoClaroAna = ("Cripto deterministica").getBytes();
      //byte[] textoClaroAna = ("Deterministica").getBytes();
      KeyPairGenerator g = KeyPairGenerator.getInstance("RSA", "BC");
      g.initialize(512);
      KeyPair kp = g.generateKeyPair();
      String[] rsa = { "RSA",                   // determinístico
        "RSA/ECB/NoPadding",                    // determinístico
        "RSA/None/NoPadding",                   // determinístico
        "RSA/None/PKCS1Padding",                // pseudo-aleatório
        "RSA/None/OAEPWithSHA1AndMGF1Padding"}; // pseudo-aleatório
      U.println("Texto claro   : " + new String(textoClaroAna));
      for (int a = 0; a < rsa.length; a++) {
        Cipher enc = Cipher.getInstance(rsa[a], "BC");
        enc.init(Cipher.ENCRYPT_MODE, kp.getPublic());
        Cipher dec = Cipher.getInstance(rsa[a], "BC");
        dec.init(Cipher.DECRYPT_MODE, kp.getPrivate());

        U.println("Encriptado com: " + enc.getAlgorithm());
        byte[][] criptograma = new byte[2][];
        for (int i = 0; i < 2; i++) {
          criptograma[i] = enc.doFinal(textoClaroAna);
          byte[] textoClaroBeto = dec.doFinal(criptograma[i]);
          U.println("Criptograma   : " + U.b2x(criptograma[i]));
          //U.println("Texto claro   : " + new String(textoClaroBeto));
        }
        //if (Arrays.equals(criptograma[0],criptograma[1])) 
        //     U.println("Iguais\n");
        //else U.println("Diferentes\n");
      }
    } catch (NoSuchAlgorithmException | NoSuchPaddingException |
            InvalidKeyException | IllegalBlockSizeException |
            BadPaddingException | NoSuchProviderException e) {
      System.out.println(e);
    }
  }
}
