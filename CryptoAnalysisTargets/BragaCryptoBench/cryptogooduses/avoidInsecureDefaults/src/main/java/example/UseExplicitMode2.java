
package example;

import org.alexmbraga.utils.U;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

// 
public final class UseExplicitMode2 {
  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException, 
          InvalidAlgorithmParameterException {

    Security.addProvider(new BouncyCastleProvider()); // provedor BC
    byte[] ptAna = ("Testing explicit operation modes").getBytes();
    
    KeyGenerator g = KeyGenerator.getInstance("DESede", "BC");
    g.init(168);
    Key k = g.generateKey();
    
    byte[] iv = new byte[8];
    SecureRandom sr = SecureRandom.getInstanceStrong(); 
    
    String[]opModes = {"DESede/OFB/NoPadding","DESede/OFB64/NoPadding",
                       "DESede/CFB/NoPadding","DESede/CFB64/NoPadding",
                       "DESede/CTR/NoPadding","DESede/CTS/NoPadding",
                       "DESede/CBC/NoPadding",};
    
    for (int a = 0; a < opModes.length; a++) {
      Cipher enc = Cipher.getInstance(opModes[a], "BC");
      Cipher dec = Cipher.getInstance(opModes[a], "BC");
      U.println("\nAlgorithm: " + enc.getAlgorithm());
      
      for (int i = 0; i < 10; i++) {
        sr.nextBytes(iv);
        enc.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv));
        byte[] ct = enc.doFinal(ptAna);
        dec.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(enc.getIV()));
        byte[] ptBeto = dec.doFinal(ct);
        U.println("Ciphertext: " + U.b2x(ct));
        U.println("Plaintext : " + new String(ptBeto));
        U.println("IV        : " + U.b2x(dec.getIV()));
      }
    }
  }
}
