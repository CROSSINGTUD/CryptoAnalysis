package example;

import example._utils.U;
import javax.crypto.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.spec.PBEKeySpec;

// 
public final class UseQualifiedNameForPBE2 {

  @SuppressWarnings("empty-statement")
  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException,
          InvalidKeySpecException, InvalidAlgorithmParameterException {

    if (args != null) {
      //char[] password = "5tr0ngP455w0rd!23".toCharArray();//8+ alfanum
      char[] password = args[0].toCharArray();//8+ alfanum

      byte[] salt = new byte[16];
      (SecureRandom.getInstanceStrong()).nextBytes(salt);

      int iterationCount = 2048; // should be 1000+
      PBEKeySpec pbeks = new PBEKeySpec(password, salt, iterationCount);
      SecretKeyFactory skf
              = SecretKeyFactory.getInstance("PBEWithHmacSHA512AndAES_256","SunJCE");
      SecretKey sk = skf.generateSecret(pbeks);
      
      Cipher c = Cipher.getInstance("PBEWithHmacSHA512AndAES_256","SunJCE");

      // Encriptação pela Ana
      c.init(Cipher.ENCRYPT_MODE, sk);
      byte[] ptAna = "Testing PBE with and AES".getBytes();
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
