
package example;

import example._utils.U;
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
public final class UseExplicitPadding2 {
  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException, 
          InvalidAlgorithmParameterException {

    Security.addProvider(new BouncyCastleProvider()); // provedor BC
    byte[] ptAna = ("Testing explicit operation modes").getBytes();
    
    String alg = "DESede";
    String[] opModes = {"OFB","OFB64","CFB","CFB64","CTR","CTS","CBC"};
    String[] paddings ={"NoPadding","PKCS5Padding","ISO10126Padding"};
    
    KeyGenerator g = KeyGenerator.getInstance(alg, "BC");
    g.init(168);
    Key k = g.generateKey();
    
    byte[] iv = new byte[8];
    SecureRandom sr = SecureRandom.getInstanceStrong();
    
    for (int a = 0; a < opModes.length; a++) {
      for (int p = 0; p < 3; p++) {
        String explicitModeAndPadding = alg+"/"+opModes[a] +"/"+ paddings[p];
        Cipher enc = Cipher.getInstance(explicitModeAndPadding, "BC");
        Cipher dec = Cipher.getInstance(explicitModeAndPadding, "BC");
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
}