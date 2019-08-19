
package example;

import example._utils.U;
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

// IVs fixos ou reutilizados
public final class UseRandomIVsForCFB128 {
  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException, 
          InvalidAlgorithmParameterException {

    Security.addProvider(new BouncyCastleProvider()); // provedor BC
    byte[] ptAna = ("Testing random IV for op modes..").getBytes();

    byte[] iv = new byte[16];
    SecureRandom.getInstanceStrong().nextBytes(iv);

    KeyGenerator g = KeyGenerator.getInstance("AES", "BC");
    g.init(256);
    Key k = g.generateKey();
    Cipher enc = Cipher.getInstance("AES/CFB128/NoPadding", "BC");
    Cipher dec = Cipher.getInstance("AES/CFB128/NoPadding", "BC");
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
