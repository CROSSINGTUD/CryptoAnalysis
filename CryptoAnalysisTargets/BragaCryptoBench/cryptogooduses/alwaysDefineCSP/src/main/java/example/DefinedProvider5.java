package example;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


public final class DefinedProvider5 {

  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException,
          InvalidAlgorithmParameterException {

    Security.addProvider(new BouncyCastleProvider());

    KeyGenerator kg = KeyGenerator.getInstance("AES","BC");
    kg.getProvider().getName();

    Cipher enc = Cipher.getInstance("AES/CTR/NoPadding","BC");
    enc.getProvider().getName();
    Cipher dec = Cipher.getInstance("AES/CTR/NoPadding","BC");
    dec.getProvider().getName();

  }
}
