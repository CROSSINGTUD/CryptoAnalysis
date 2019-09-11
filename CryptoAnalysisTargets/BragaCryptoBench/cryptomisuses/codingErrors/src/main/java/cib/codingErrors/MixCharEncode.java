package cib.codingErrors;

import org.alexmbraga.utils.U;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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

// Testing for diverse char encodings
// here the cipher textis not saved to string, 
// the plaintext is shown in differet encodings 
// all is utf-8, but the last string is utf-16
public final class MixCharEncode {

  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException,
          InvalidAlgorithmParameterException, UnsupportedEncodingException {

    Security.addProvider(new BouncyCastleProvider()); // provedor BC
    byte[] ptAna = ("Teste de String.").getBytes(StandardCharsets.UTF_8);

    byte[] iv = new byte[16];
    (new SecureRandom()).nextBytes(iv);

    KeyGenerator g = KeyGenerator.getInstance("AES", "BC");
    g.init(128);
    Key k = g.generateKey();
    String aes = "AES/CTR/NoPadding";
   
    Cipher enc = Cipher.getInstance(aes, "BC");
    Cipher dec = Cipher.getInstance(aes, "BC");
    byte[] ct;

    enc.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv));
    ct = enc.doFinal(ptAna);
    
    dec.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv));
    byte[] ptBeto = dec.doFinal(ct);
    U.println("Encoded cipher text: " + U.b2x(ct));
    U.println("Plain text: " + new String(ptBeto, StandardCharsets.UTF_8));
    U.println("");
    dec.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv));
    ptBeto = dec.doFinal(ct);
    U.println("Original cipher text: " + U.b2x(ct));
    U.println("Plain text: " + new String(ptBeto, StandardCharsets.UTF_16));
    
  }
}
