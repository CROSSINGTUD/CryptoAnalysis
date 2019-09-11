
package pkm.keyReuseInStreamCipher;

import org.alexmbraga.utils.U;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

//
public final class KeyReuseStreamCipher5 {

  public static void main(String args[]) { 
    try {
      Security.addProvider(new BouncyCastleProvider()); // provedor BC
    byte[][] M = {("Reuso de chave d").getBytes(),
                  ("a Cifra de fluxo").getBytes()};
    
    SecureRandom sr = new SecureRandom();
    
    byte[] iv= new byte[16], k = new byte[16];
    
    sr.nextBytes(k);
    sr.nextBytes(iv);
    
    byte[][] C = new byte[2][];

    SecretKeySpec ks = new SecretKeySpec(k, "AES");
    Cipher enc = Cipher.getInstance("AES/OFB/NoPadding", "BC");

    enc.init(Cipher.ENCRYPT_MODE, ks, new IvParameterSpec(iv));
    C[0] = enc.doFinal(M[0]);
    enc.init(Cipher.ENCRYPT_MODE, ks, new IvParameterSpec(iv));
    C[1] = enc.doFinal(M[1]);

    byte[] M0xorM1 = U.x_or(C[0], C[1]);
    byte[] M1 = U.x_or(M[0], M0xorM1);

    U.println("\nReusando o fluxo de chaves pelo reuso do IV no CTR");
    U.println("Encriptado com: " + enc.getAlgorithm());
    U.println("C[" + 0 + "] =" + U.b2x(C[0]));
    U.println("iv[" + 0 + "]=" + U.b2x(iv));
    U.println("C[" + 1 + "] =" + U.b2x(C[1]));
    U.println("iv[" + 1 + "]=" + U.b2x(iv));
    U.println("C0^C1 = k^M0^K^M1 = M0^M1 =" + U.b2x(M0xorM1));
    U.println("M0^M1^M0 = M1 = " + U.b2s(M1));
    } catch (NoSuchAlgorithmException | NoSuchProviderException | 
            NoSuchPaddingException | InvalidKeyException | 
            InvalidAlgorithmParameterException | IllegalBlockSizeException | 
            BadPaddingException ex) { }}
}
