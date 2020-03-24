package example;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class DoNotReuseKeyStreamCipher1 {

  public static void main(String args[]) {
    try {
      Security.addProvider(new BouncyCastleProvider());
      byte[][] M = {("first text").getBytes(),
                    ("second text").getBytes()};
      
      byte[][] iv = new byte[2][16];
      byte[][] C  = new byte[2][];

      SecureRandom sr = SecureRandom.getInstanceStrong();
      sr.nextBytes(iv[0]);
      sr.nextBytes(iv[1]);
      
      KeyGenerator g = KeyGenerator.getInstance("AES", "BC");
      g.init(256);
      Key k = g.generateKey();
      
      
      Cipher enc = Cipher.getInstance("AES/OFB/NoPadding", "BC");

      enc.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv[0]));
      C[0] = enc.doFinal(M[0]);
      enc.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv[1]));
      C[1] = enc.doFinal(M[1]);

    } catch (NoSuchAlgorithmException | NoSuchProviderException |
            NoSuchPaddingException | InvalidKeyException |
            InvalidAlgorithmParameterException | IllegalBlockSizeException |
            BadPaddingException ex) {}
  }
}
