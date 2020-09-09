package example;

import java.io.UnsupportedEncodingException;
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


public final class DoNotSaveToString {

  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException,
          InvalidAlgorithmParameterException, UnsupportedEncodingException {

    Security.addProvider(new BouncyCastleProvider());
    byte[] ptA = ("Testing String").getBytes("UTF-8");

    byte[] iv = new byte[16];
    SecureRandom.getInstanceStrong().nextBytes(iv);

    KeyGenerator kg = KeyGenerator.getInstance("AES", "BC");
    kg.init(128);
    Key key = kg.generateKey();
    String alg = "AES/CTR/NoPadding";
    
    Cipher enc = Cipher.getInstance(alg,"BC");
    Cipher dec = Cipher.getInstance(alg,"BC");
    byte[] ct;

    enc.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
    ct = enc.doFinal(ptA);
    
    dec.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
    byte[] ptB = dec.doFinal(ct);
    dec.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
    ptB = dec.doFinal(ct);
    
  }
}
