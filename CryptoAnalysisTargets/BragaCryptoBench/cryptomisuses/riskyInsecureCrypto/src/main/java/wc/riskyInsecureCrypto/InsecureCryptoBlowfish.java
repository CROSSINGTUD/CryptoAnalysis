package wc.riskyInsecureCrypto;

import static org.alexmbraga.utils.U.x2s;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;

public final class InsecureCryptoBlowfish {

  public static void main(String args[])
          throws
          NoSuchAlgorithmException,
          NoSuchPaddingException,
          InvalidKeyException,
          BadPaddingException,
          IllegalBlockSizeException,
          NoSuchProviderException,
          InvalidAlgorithmParameterException {
    byte[] mensagem = "This is a test Blowfsh".getBytes();

    KeyGenerator kg = KeyGenerator.getInstance("Blowfish","SunJCE");
    kg.init(128);
    byte[] iv = new byte[8];
    (new SecureRandom()).nextBytes(iv);
    Key k = kg.generateKey();
    // Cria a implementacao de DES
    Cipher c = Cipher.getInstance("Blowfish/CTR/NoPadding","SunJCE");
        // Criptografando a mensagem
    // inicializa o algoritmo para criptografia
    AlgorithmParameterSpec aps = new IvParameterSpec(iv);
    c.init(Cipher.ENCRYPT_MODE, k, aps);
    // criptografa o texto inteiro
    byte[] ct = c.doFinal(mensagem);
    System.out.println("Cifrado com " + c.getAlgorithm());
    System.out.println("A mensagem cifrada fica:");
    System.out.println(x2s(ct));
    
    c.init(Cipher.DECRYPT_MODE, k, aps);
    byte[] pt = c.doFinal(ct);
    System.out.println("A mensagem original era:" + new String(pt));
  }

}
