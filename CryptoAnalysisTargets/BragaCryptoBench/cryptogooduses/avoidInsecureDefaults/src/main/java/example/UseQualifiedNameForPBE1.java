package example;

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

    Security.addProvider(new BouncyCastleProvider());

    if (args != null) {
      char[] password = args[0].toCharArray();

      byte[] salt = new byte[16];
      (SecureRandom.getInstanceStrong()).nextBytes(salt);

      int iterationCount = 2048;
      PBEKeySpec pbeks = new PBEKeySpec(password, salt, iterationCount);
      SecretKeyFactory skf
              = SecretKeyFactory.getInstance("PBEWithSHA256And128BitAES-CBC-BC", "BC");
      Key sk = skf.generateSecret(pbeks);
      Cipher c = Cipher.getInstance("AES/CTR/NoPadding", "BC");

      c.init(Cipher.ENCRYPT_MODE, sk);
      byte[] ptA = "Testing AES".getBytes();
      byte[] ct = c.doFinal(ptA);

      c.init(Cipher.DECRYPT_MODE, sk);
      byte[] ptB = c.doFinal(ct);

    }
  }
}
