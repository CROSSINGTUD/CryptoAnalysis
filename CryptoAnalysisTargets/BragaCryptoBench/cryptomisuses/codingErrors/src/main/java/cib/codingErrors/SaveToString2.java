package cib.codingErrors;

import _utils.U;
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

//
public final class SaveToString2 {

  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException,
          InvalidAlgorithmParameterException, UnsupportedEncodingException {

    Security.addProvider(new BouncyCastleProvider()); // provedor BC
    byte[] ptAna = ("Teste de String.").getBytes("UTF-8");

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
    
    //here is the misuse: uses the default encoding that has misbehavior.
    String ct2str = new String(ct);
    
    dec.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv));
    byte[] ptBeto = dec.doFinal(ct2str.getBytes("UTF-8"));
    U.println("Criptograma encoded  : " + U.b2x(ct2str.getBytes("UTF-8")));
    U.println("Texto claro   : " + new String(ptBeto));
    U.println("");
    dec.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv));
    ptBeto = dec.doFinal(ct);
    U.println("Criptograma original : " + U.b2x(ct));
    U.println("Texto claro   : " + new String(ptBeto));
    
  }
}
