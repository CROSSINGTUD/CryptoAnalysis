package pdf.insecureStreamCipher;

import org.alexmbraga.utils.U;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class ConfusingBlockAndStream {

  public static void main(String args[]) {
    try {
      Security.addProvider(new BouncyCastleProvider()); // provedor BC
      byte[][] M = {("Troca a cifra de").getBytes(),
                    ("bloco por fluxo.").getBytes()};
      byte[][] C = new byte[2][], iv = new byte[2][];

      byte[] k = U.x2b("00112233445566778899AABBCCDDEEFF");
      byte[] seed = U.x2b("0123456789ABCDEF0123456789ABCDEF");

      SecretKeySpec ks = new SecretKeySpec(k, "AES");
      //Cipher enc = Cipher.getInstance("AES/CBC/NoPadding", "BC");
      Cipher enc = Cipher.getInstance("AES/OFB/NoPadding", "BC");

      SecureRandom sr = new SecureRandom();
      sr.setSeed(seed);
      enc.init(Cipher.ENCRYPT_MODE, ks, sr);
      C[0] = enc.doFinal(M[0]);
      iv[0] = enc.getIV();

      sr = new SecureRandom();
      sr.setSeed(seed);
      enc.init(Cipher.ENCRYPT_MODE, ks, sr);
      C[1] = enc.doFinal(M[1]);
      iv[1] = enc.getIV();

      byte[] M0xorM1 = U.x_or(C[0], C[1]);
      byte[] M1 = U.x_or(M[0], M0xorM1);

      U.println("Reusando o fluxo de chaves pelo reuso da semente no OFB");
      U.println("M0 = "+ U.b2s(M[0])+"; M1 = "+ U.b2s(M[1]));
      U.println("Encriptado com: " + enc.getAlgorithm());
      U.println("C[" + 0 + "] =" + U.b2x(C[0]));
      U.println("iv[" + 0 + "]=" + U.b2x(iv[0]));
      U.println("C[" + 1 + "] =" + U.b2x(C[1]));
      U.println("iv[" + 1 + "]=" + U.b2x(iv[1]));
      U.println("C0^C1 = k^M0^K^M1 = M0^M1 = " + U.b2x(M0xorM1));
      U.println("M0^M1^M0 = M1 = " + U.b2s(M1));

    } catch (NoSuchAlgorithmException | NoSuchProviderException |
            NoSuchPaddingException ex) {
    } catch (InvalidKeyException |
            IllegalBlockSizeException | BadPaddingException ex) {
    }
  }
}
