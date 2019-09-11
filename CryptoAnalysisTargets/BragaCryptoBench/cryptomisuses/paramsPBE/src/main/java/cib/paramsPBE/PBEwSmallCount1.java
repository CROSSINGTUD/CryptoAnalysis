package cib.paramsPBE;

import org.alexmbraga.utils.U;
import javax.crypto.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.spec.PBEKeySpec;
import org.bouncycastle.jce.provider.*;

// 
public final class PBEwSmallCount1 {

  @SuppressWarnings("empty-statement")
  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException,
          InvalidKeySpecException, InvalidAlgorithmParameterException {

    Security.addProvider(new BouncyCastleProvider()); // provedor BC

    // configurações do PBE comuns para Ana e Bato
    if (args != null) {
      //char[] senha = "5enha!23".toCharArray();//8+ alfanum
      char[] senha = args[0].toCharArray();//8+ alfanum

      byte[] salt = new byte[16];
      (SecureRandom.getInstanceStrong()).nextBytes(salt);

      int iterationCount = 20; // should be 1000+
      PBEKeySpec pbeks = new PBEKeySpec(senha, salt, iterationCount);
      SecretKeyFactory skf
              = SecretKeyFactory.getInstance("PBEWithSHA256And128BitAES-CBC-BC", "BC");
      Key sk = skf.generateSecret(pbeks);
      Cipher c = Cipher.getInstance("AES/CTR/NoPadding", "BC");

      // Encriptação pela Ana
      c.init(Cipher.ENCRYPT_MODE, sk);
      byte[] textoclaroAna = "Testando o AES..".getBytes();
      byte[] criptograma = c.doFinal(textoclaroAna);

      // decriptação pelo Beto
      c.init(Cipher.DECRYPT_MODE, sk); //inicializa o AES para decriptacao
      byte[] textoclaroBeto = c.doFinal(criptograma); // Decriptando

      U.println("Chave gerada com: " + skf.getAlgorithm());
      U.println("Encriptado   com: " + c.getAlgorithm());
      //U.println("Chave criptográfica: " + U.b2x(sk.getEncoded()));
      U.println("Init Vector gerado : " + U.b2x(c.getIV()));
      U.println("Criptograma (A-->B): " + U.b2x(criptograma));
      U.println("Texto claro do Beto: " + new String(textoclaroBeto));
    }
  }
}
