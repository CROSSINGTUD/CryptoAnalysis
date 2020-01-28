
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


public final class GenerateRandomIV {
  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException, 
          InvalidAlgorithmParameterException {

    Security.addProvider(new BouncyCastleProvider());
    byte[] ptA = ("Test String").getBytes();
    
    byte[] iv = new byte[16];
    SecureRandom.getInstanceStrong().nextBytes(iv);  
    
    KeyGenerator kg = KeyGenerator.getInstance("AES", "BC");
    kg.init(128);
    Key key = kg.generateKey();
    String[] algs = {"AES/OFB/NoPadding", "AES/CFB/NoPadding", "AES/CTR/NoPadding"};
    boolean fixIV = true;
    for (int a = 0; a < algs.length; a++) {
      Cipher enc = Cipher.getInstance(algs[a], "BC");
      Cipher dec = Cipher.getInstance(algs[a], "BC");
      byte[][] ct = new byte[2][];
      for (int i = 0; i < 2; i++) {
        enc.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        ct[i] = enc.doFinal(ptA);
        dec.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] ptB = dec.doFinal(ct[i]);
        if (!fixIV) iv[iv.length-1] = (byte) (iv[iv.length-1]^0x01);
        
      }
    }
  }
}
