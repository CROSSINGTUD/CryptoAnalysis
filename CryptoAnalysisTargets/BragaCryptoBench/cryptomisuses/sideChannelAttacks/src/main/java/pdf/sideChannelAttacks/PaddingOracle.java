package pdf.sideChannelAttacks;

import _utils.U;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class PaddingOracle {

  private static final byte[] k = U.x2b("00112233445566778899AABBCCDDEEFF");
  public  static final byte[] iv = U.x2b("0123456789ABCDEF0123456789ABCDEF");
  private static final SecretKeySpec ks = new SecretKeySpec(k, "AES");
  static {Security.addProvider(new BouncyCastleProvider());}

  public static boolean oracle(byte[] iv, byte[] c) {
    boolean ok = true;
    try {
      Cipher enc = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
      enc.init(Cipher.DECRYPT_MODE, ks, new IvParameterSpec(iv));
      enc.doFinal(c); // ignora a saída do doFinal()!!!!
    } catch (BadPaddingException e) {
      ok = false;
    } catch (NoSuchAlgorithmException | NoSuchProviderException |
            NoSuchPaddingException | InvalidKeyException |
            InvalidAlgorithmParameterException |
            IllegalBlockSizeException ex) { /*ex.printStackTrace(); */}
    return ok;
  }

  public static byte[] encripta() {
    byte[] criptobloco = null;
    try {
      Cipher enc = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
      enc.init(Cipher.ENCRYPT_MODE, ks, new IvParameterSpec(iv));
      criptobloco = enc.doFinal(textoClaro);
    } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException |
            NoSuchProviderException | NoSuchPaddingException |
            InvalidKeyException | IllegalBlockSizeException |
            BadPaddingException ex) { /* faz nada */ }
    return criptobloco;
  }
  
  //private static final byte[] textoClaro = ("S").getBytes(); //incompleto
  //private static final byte[] textoClaro = ("SB").getBytes(); //incompleto
  //private static final byte[] textoClaro = ("SBS").getBytes(); //incompleto
  //private static final byte[] textoClaro = ("SBSe").getBytes(); //incompleto
  //private static final byte[] textoClaro = ("SBSeg").getBytes(); //incompleto
  //private static final byte[] textoClaro = ("SBSeg'").getBytes(); //incompleto
  //private static final byte[] textoClaro = ("SBSeg'2").getBytes();
  //private static final byte[] textoClaro = ("SBSeg'20").getBytes();
  //private static final byte[] textoClaro = ("SBSeg'201").getBytes();
  //private static final byte[] textoClaro = ("SBSeg'2015").getBytes();
  //private static final byte[] textoClaro = ("SBSeg'2015,").getBytes();
  //private static final byte[] textoClaro = ("SBSeg'2015, ").getBytes();
  //private static final byte[] textoClaro = ("SBSeg'2015, M").getBytes();
  //private static final byte[] textoClaro = ("SBSeg'2015, MC").getBytes();
  private static final byte[] textoClaro = ("SBSeg'2015, MC1").getBytes();
  //private static final byte[] textoClaro = ("SBSeg'2015, MC1.").getBytes();
}
