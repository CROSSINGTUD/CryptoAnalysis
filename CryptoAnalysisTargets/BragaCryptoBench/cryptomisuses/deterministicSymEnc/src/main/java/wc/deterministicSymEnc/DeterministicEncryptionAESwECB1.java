package wc.deterministicSymEnc;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


public final class DeterministicEncryptionAESwECB1 {

  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException {

    Security.addProvider(new BouncyCastleProvider()); // provedor BC
    byte[] textoClaroAna = ("Deterministica..").getBytes();
    KeyGenerator g = KeyGenerator.getInstance("AES", "BC");
    g.init(256);
    Key k = g.generateKey();
    
      Cipher enc = Cipher.getInstance("AES/ECB/NoPadding", "BC");
      enc.init(Cipher.ENCRYPT_MODE, k);
      Cipher dec = Cipher.getInstance("AES/ECB/NoPadding", "BC");
      dec.init(Cipher.DECRYPT_MODE, k);

      byte[][] criptograma = new byte[2][];
      for (int i = 0; i < 2; i++) {
        criptograma[i] = enc.doFinal(textoClaroAna);
        byte[] textoClaroBeto = dec.doFinal(criptograma[i]);
      }
      
      enc = Cipher.getInstance("AES/ECB/PKCS5Padding", "BC");
      enc.init(Cipher.ENCRYPT_MODE, k);
      dec = Cipher.getInstance("AES/ECB/PKCS5Padding", "BC");
      dec.init(Cipher.DECRYPT_MODE, k);

      criptograma = new byte[2][];
      for (int i = 0; i < 2; i++) {
        criptograma[i] = enc.doFinal(textoClaroAna);
        byte[] textoClaroBeto = dec.doFinal(criptograma[i]);
      }
      
      
      enc = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
      enc.init(Cipher.ENCRYPT_MODE, k);
      dec = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
      dec.init(Cipher.DECRYPT_MODE, k);

      criptograma = new byte[2][];
      for (int i = 0; i < 2; i++) {
        criptograma[i] = enc.doFinal(textoClaroAna);
        byte[] textoClaroBeto = dec.doFinal(criptograma[i]);
      }
      
      
  }
}
