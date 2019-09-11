package pdf.insecureDefault;

import org.alexmbraga.utils.U;
import javax.crypto.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.spec.PBEKeySpec;
import org.bouncycastle.jce.provider.*;

// 
public final class InsecureDefaultPBE {

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
      (new SecureRandom()).nextBytes(salt);

      int iterationCount = 2048; // 1000+
      PBEKeySpec pbeks = new PBEKeySpec(senha, salt, iterationCount);
      SecretKeyFactory skf
              = SecretKeyFactory.getInstance("PBE","SunJCE");
      Key sk = skf.generateSecret(pbeks);

      U.println("Chave gerada com: " + skf.getAlgorithm());
      U.println("Chave gerada com: " + skf.getClass().getName());

      //U.println("Chave criptográfica: " + U.b2x(sk.getEncoded()));

    }
  }
}
