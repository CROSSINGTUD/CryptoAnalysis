package example;

import org.alexmbraga.utils.U;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class UseCBC {

  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException,
          InvalidAlgorithmParameterException {

    Security.addProvider(new BouncyCastleProvider()); // provedor BC
    byte[] ptAna = ("Use CBC with IV.").getBytes();

    KeyGenerator g = KeyGenerator.getInstance("AES", "BC");
    g.init(256);
    Key k = g.generateKey();

    Cipher enc = Cipher.getInstance("AES/CBC/NoPadding", "BC");
    Cipher dec = Cipher.getInstance("AES/CBC/NoPadding", "BC");
    U.println("Algorithm: " + enc.getAlgorithm());
    byte[] ct;
    byte[] iv = new byte[16];
    SecureRandom sr = SecureRandom.getInstanceStrong();

    for (int i = 0; i < 10; i++) {
      sr.nextBytes(iv);
      enc.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv));
      ct = enc.doFinal(ptAna);
      dec.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(enc.getIV()));
      byte[] ptBeto = dec.doFinal(ct);
      U.println("Ciphertext: " + U.b2x(ct));
      U.println("Plaintext : " + new String(ptBeto));
      U.println("IV        : " + U.b2x(enc.getIV()));
    }
  }
}
