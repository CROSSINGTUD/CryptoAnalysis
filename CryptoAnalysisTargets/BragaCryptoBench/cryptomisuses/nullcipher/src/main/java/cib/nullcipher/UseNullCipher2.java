package cib.nullcipher;

import org.alexmbraga.utils.U;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.NullCipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

// 
public final class UseNullCipher2 {

  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException,
          InvalidAlgorithmParameterException {

    Security.addProvider(new BouncyCastleProvider()); // provedor BC
    byte[] ptAna = ("Teste de IV fixoTeste de IV fixo").getBytes();

    byte[] iv = new byte[16];
    (new SecureRandom()).nextBytes(iv);

    KeyGenerator g = KeyGenerator.getInstance("AES", "BC");
    g.init(128);
    Key k = g.generateKey();
    String[] aes = {"AES/OFB/NoPadding", "AES/CFB/NoPadding", "AES/CTR/NoPadding"};
    boolean fixIV = true;
    U.println("Texto claro: " + new String(ptAna));
    //U.println("Chave      : " + U.b2x(k.getEncoded()));
    U.println("Iv fixo    : " + U.b2x(iv) + "\n");
    for (int a = 0; a < aes.length; a++) {
      Cipher enc = new NullCipher();
      Cipher dec = new javax.crypto.NullCipher();
      U.println("Encriptado com: " + enc.getAlgorithm());
      byte[][] ct = new byte[2][];
      for (int i = 0; i < 2; i++) {
        enc.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv));
        ct[i] = enc.doFinal(ptAna);
        dec.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv));
        byte[] ptBeto = dec.doFinal(ct[i]);
        U.println("Criptograma   : " + U.b2x(ct[i]));
        U.println("Texto claro   : " + new String(ptBeto));
        if (!fixIV) {
          iv[iv.length - 1] = (byte) (iv[iv.length - 1] ^ 0x01);
        }
      }
      if (MessageDigest.isEqual(ct[0], ct[1])) {
        U.println("Iguais\n");
      } else {
        U.println("Diferentes\n");
      }
    }
  }
}
