package example;

import org.alexmbraga.utils.U;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

// iv não aleatorio ou reutilizado no modo CBC 
public final class UseRandomIVsForCTS {

  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException,
          InvalidAlgorithmParameterException {

    byte[] ptAna = ("Testing random IV for op modes..").getBytes();

    byte[] iv = new byte[16];
    SecureRandom.getInstanceStrong().nextBytes(iv);

    KeyGenerator g = KeyGenerator.getInstance("AES","SunJCE");
    g.init(256);
    Key k = g.generateKey();
    Cipher enc = Cipher.getInstance("AES/CTS/NoPadding","SunJCE");
    Cipher dec = Cipher.getInstance("AES/CTS/NoPadding","SunJCE");
    U.println("Encriptado com: " + enc.getAlgorithm());
    byte[] ct;
    for (int i = 0; i < 5; i++) {
      SecureRandom.getInstanceStrong().nextBytes(iv);
      enc.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv));
      ct = enc.doFinal(ptAna);
      dec.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(enc.getIV()));
      byte[] ptBeto = dec.doFinal(ct);
      U.println("Ciphertext: " + U.b2x(ct));
      U.println("Plaintext : " + new String(ptBeto));
      U.println("IV        : " + U.b2x(dec.getIV()));
    }
  }
}
