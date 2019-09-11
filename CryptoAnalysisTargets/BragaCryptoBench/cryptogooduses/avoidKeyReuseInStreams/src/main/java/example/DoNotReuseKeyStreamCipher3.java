package example;

import org.alexmbraga.utils.U;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


public final class DoNotReuseKeyStreamCipher3 {

  public static void main(String args[]) {
    try {
      Security.addProvider(new BouncyCastleProvider()); // provedor BC
      byte[][] M = {("Reuso de chave d").getBytes(),
        ("a Cifra de fluxo").getBytes()};
      byte[] iv1 = new byte[16];
      byte[] iv2 = new byte[16];

      byte[][] C = new byte[2][];

      SecureRandom sr = SecureRandom.getInstanceStrong();
      sr.nextBytes(iv1);
      sr.nextBytes(iv2);

      KeyGenerator g = KeyGenerator.getInstance("AES", "BC");
      g.init(256);
      Key k = g.generateKey();
      Cipher enc = Cipher.getInstance("AES/OFB/NoPadding", "BC");

      enc.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv1));
      C[0] = enc.doFinal(M[0]);
      //U.stdIncIV(iv[1],iv[1].length/2); // incremento de iv no CTR
      enc.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv2));
      C[1] = enc.doFinal(M[1]);

      byte[] M0xorM1 = U.x_or(C[0], C[1]);
      byte[] M1 = U.x_or(M[0], M0xorM1);

      U.println("\nReusando o fluxo de chaves pelo reuso do IV no CTR");
      U.println("Encriptado com: " + enc.getAlgorithm());
      U.println("C[" + 0 + "] =" + U.b2x(C[0]));
      U.println("iv[" + 0 + "]=" + U.b2x(iv1));
      U.println("C[" + 1 + "] =" + U.b2x(C[1]));
      U.println("iv[" + 1 + "]=" + U.b2x(iv2));
      U.println("C0^C1 = k^M0^K^M1 = M0^M1 =" + U.b2x(M0xorM1));
      U.println("M0^M1^M0 = M1 = " + U.b2s(M1));
    } catch (NoSuchAlgorithmException | NoSuchProviderException |
            NoSuchPaddingException | InvalidKeyException |
            InvalidAlgorithmParameterException | IllegalBlockSizeException |
            BadPaddingException ex) {
    }
  }
}
