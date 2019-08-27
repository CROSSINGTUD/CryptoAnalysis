package pkm.keyReuseInStreamCipher;

import _utils.U;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

//
public final class KeyReuseStreamCipher1 {

  public static void main(String args[]) {
    try {
      Security.addProvider(new BouncyCastleProvider()); // provedor BC
      byte[][] M = {("Reuso de chave d").getBytes(),
        ("a Cifra de fluxo").getBytes()};
      byte[][] iv = {U.x2b("0123456789ABCDEF0123456789ABCDEF"),
                     U.x2b("0123456789ABCDEF0123456789ABCDEF")};
      byte[] k = U.x2b("00112233445566778899AABBCCDDEEFF");
      byte[][] C = new byte[2][];

      SecretKeySpec ks = new SecretKeySpec(k, "AES");
      Cipher enc = Cipher.getInstance("AES/OFB/NoPadding", "BC");

      enc.init(Cipher.ENCRYPT_MODE, ks, new IvParameterSpec(iv[0]));
      C[0] = enc.doFinal(M[0]);
      enc.init(Cipher.ENCRYPT_MODE, ks, new IvParameterSpec(iv[1]));
      C[1] = enc.doFinal(M[1]);

      byte[] M0xorM1 = U.x_or(C[0], C[1]);
      byte[] M1 = U.x_or(M[0], M0xorM1);

      U.println("\nReusando o fluxo de chaves pelo reuso do IV no CTR");
      U.println("Encriptado com: " + enc.getAlgorithm());
      U.println("C[" + 0 + "] =" + U.b2x(C[0]));
      U.println("iv[" + 0 + "]=" + U.b2x(iv[0]));
      U.println("C[" + 1 + "] =" + U.b2x(C[1]));
      U.println("iv[" + 1 + "]=" + U.b2x(iv[1]));
      U.println("C0^C1 = k^M0^K^M1 = M0^M1 =" + U.b2x(M0xorM1));
      U.println("M0^M1^M0 = M1 = " + U.b2s(M1));
    } catch (NoSuchAlgorithmException | NoSuchProviderException |
            NoSuchPaddingException | InvalidKeyException |
            InvalidAlgorithmParameterException | IllegalBlockSizeException |
            BadPaddingException ex) {
    }
  }
}
