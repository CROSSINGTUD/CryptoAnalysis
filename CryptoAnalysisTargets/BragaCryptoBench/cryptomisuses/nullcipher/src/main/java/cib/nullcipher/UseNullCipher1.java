package cib.nullcipher;

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
import javax.crypto.NullCipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

// 
public final class UseNullCipher1 {

  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException,
          InvalidAlgorithmParameterException {

    Security.addProvider(new BouncyCastleProvider()); // provedor BC
    byte[] ptAna = ("Teste NullCipher").getBytes();

    byte[] iv = new byte[16];
    (new SecureRandom()).nextBytes(iv);

    KeyGenerator g = KeyGenerator.getInstance("AES", "BC");
    g.init(128);
    Key k = g.generateKey();

    U.println("Texto : " + new String(ptAna));
    //U.println("Chave : " + U.b2x(k.getEncoded()));
    U.println("IV    : " + U.b2x(iv) + "\n");

    Cipher enc = new NullCipher();
    Cipher dec = new javax.crypto.NullCipher();
    U.println("Encriptado com: " + enc.getAlgorithm());
    byte[] ct;
    enc.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv));
    ct = enc.doFinal(ptAna);
    dec.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv));
    byte[] ptBeto = dec.doFinal(ct);
    U.println("Criptograma : " + U.b2x(ct));
    U.println("Texto claro : " + new String(ptBeto));
    U.println("Texto hex   : " + U.b2x(ptBeto));
  }
}
