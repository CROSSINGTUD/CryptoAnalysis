package wc.riskyInsecureCrypto;

import static _utils.U.x2s;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;

public final class InsecureCryptoDES {

  public static void main(String args[])
          throws
          NoSuchAlgorithmException,
          NoSuchPaddingException,
          InvalidKeyException,
          BadPaddingException,
          IllegalBlockSizeException,
          NoSuchProviderException,
          InvalidAlgorithmParameterException {
    byte[] mensagem = "Este é um teste do DES".getBytes();
    // Gerando uma k DES de 56 bits
    KeyGenerator kg = KeyGenerator.getInstance("DES","SunJCE");
    kg.init(56);
    byte[] iv = new byte[8];
    (new SecureRandom()).nextBytes(iv);
    Key k = kg.generateKey();
    // Cria a implementacao de DES
    Cipher c = Cipher.getInstance("DES/CTR/NoPadding","SunJCE");
        // Criptografando a mensagem
    // inicializa o algoritmo para criptografia
    AlgorithmParameterSpec aps = new IvParameterSpec(iv);
    c.init(Cipher.ENCRYPT_MODE, k, aps);
    // criptografa o texto inteiro
    byte[] ct = c.doFinal(mensagem);
    System.out.println("Cifrado com " + c.getAlgorithm());
    System.out.println("A mensagem cifrada fica:");
    System.out.println(x2s(ct));
    // Descriptografando a mensagem
    c.init(Cipher.DECRYPT_MODE, k);
    byte[] pt = c.doFinal(ct);
    System.out.println("A mensagem original era:" + new String(pt));
  }

}
