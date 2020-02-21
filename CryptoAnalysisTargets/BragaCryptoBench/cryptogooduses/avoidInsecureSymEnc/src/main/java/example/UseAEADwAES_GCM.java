package example;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import org.bouncycastle.jce.provider.*;

public final class UseAEADwAES_GCM {

  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException,
          InvalidAlgorithmParameterException {

    Security.addProvider(new BouncyCastleProvider());

    byte[] iv = new byte[128];
    SecureRandom.getInstanceStrong().nextBytes(iv);
    
    KeyGenerator g = KeyGenerator.getInstance("AES", "BC");
    g.init(256);
    Key k = g.generateKey();
    
    GCMParameterSpec gps = new GCMParameterSpec(128, iv);
    Cipher c = Cipher.getInstance("AES/GCM/NoPadding", "BC");

    c.init(Cipher.ENCRYPT_MODE, k, gps);
    byte[] ptA = "This is a demo text".getBytes();
    c.updateAAD("AAD is not encripted".getBytes());
    byte[] ciphertext = c.doFinal(ptA);
    
    c.init(Cipher.DECRYPT_MODE, k, gps);
    c.updateAAD("AAD is not encripted".getBytes());
    boolean ok = true;
    byte[] ptB = null;
    try {
      ptB = c.doFinal(ciphertext);
    } catch (AEADBadTagException e) {
      ok = false;
    }

  }
}