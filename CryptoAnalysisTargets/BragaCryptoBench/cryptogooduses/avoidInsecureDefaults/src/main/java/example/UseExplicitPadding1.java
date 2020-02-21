
package example;

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
public final class UseExplicitPadding1 {
  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException, 
          InvalidAlgorithmParameterException {

    Security.addProvider(new BouncyCastleProvider());
    byte[] ptA = ("Testing explicit operation modes").getBytes();
    
    KeyGenerator g = KeyGenerator.getInstance("AES", "BC");
    g.init(256);
    Key k = g.generateKey();
    byte[] iv = new byte[16];
    SecureRandom sr = SecureRandom.getInstanceStrong();
    
    String[] opModes = {"AES/OFB","AES/OFB128","AES/CFB","AES/CFB128",
                        "AES/CTR","AES/CTS","AES/CBC"};
    String[] paddings ={"NoPadding","PKCS5Padding","ISO10126Padding"};
    
    for (int a = 0; a < opModes.length; a++) {
      for (int p = 0; p < 3; p++) {
        String explicitModeAndPadding = opModes[a] +"/"+ paddings[p];
        Cipher enc = Cipher.getInstance(explicitModeAndPadding, "BC");
        Cipher dec = Cipher.getInstance(explicitModeAndPadding, "BC");

        for (int i = 0; i < 10; i++) {
          sr.nextBytes(iv);
          enc.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv));
          byte[] ct = enc.doFinal(ptA);
          dec.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(enc.getIV()));
          byte[] ptB = dec.doFinal(ct);
          
        }
      }
    }
  }
}
