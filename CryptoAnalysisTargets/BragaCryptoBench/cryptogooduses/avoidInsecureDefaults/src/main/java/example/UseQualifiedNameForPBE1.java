package example;

import example._utils.U;
import javax.crypto.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.spec.PBEKeySpec;
import org.bouncycastle.jce.provider.*;

// 
public final class UseQualifiedNameForPBE1 {

  @SuppressWarnings("empty-statement")
  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException,
          InvalidKeySpecException, InvalidAlgorithmParameterException {

    Security.addProvider(new BouncyCastleProvider()); // provedor BC

    // configurações do PBE comuns para Ana e Bato
    if (args != null) {
      //char[] senha = "5tr0ngP455w0rd!23".toCharArray();//8+ alfanum
      char[] senha = args[0].toCharArray();//8+ alfanum

      byte[] salt = new byte[16];
      (SecureRandom.getInstanceStrong()).nextBytes(salt);

      int iterationCount = 2048; // should be 1000+
      PBEKeySpec pbeks = new PBEKeySpec(senha, salt, iterationCount);
      SecretKeyFactory skf
              = SecretKeyFactory.getInstance("PBEWithSHA256And128BitAES-CBC-BC", "BC");
      Key sk = skf.generateSecret(pbeks);
      Cipher c = Cipher.getInstance("AES/CTR/NoPadding", "BC");

      // Encriptação pela Ana
      c.init(Cipher.ENCRYPT_MODE, sk);
      byte[] ptAna = "Testando o AES..".getBytes();
      byte[] ct = c.doFinal(ptAna);

      // decriptação pelo Beto
      c.init(Cipher.DECRYPT_MODE, sk); //inicializa o AES para decriptacao
      byte[] ptBeto = c.doFinal(ct); // Decriptando

      U.println("Key Gen. Alg.  : " + skf.getAlgorithm());
      U.println("Encryption Alg.: " + c.getAlgorithm());
      //U.println("Encryption Key : " + U.b2x(sk.getEncoded()));
      U.println("Init. Vector   : " + U.b2x(c.getIV()));
      U.println("Ciphertext     : " + U.b2x(ct));
      U.println("Plaintext: " + new String(ptBeto));
    }
  }
}
