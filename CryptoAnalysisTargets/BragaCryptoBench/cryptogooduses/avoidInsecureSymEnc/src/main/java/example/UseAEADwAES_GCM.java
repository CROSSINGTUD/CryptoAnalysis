package example;

import example._utils.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import org.bouncycastle.jce.provider.*;

public final class UseAEADwAES_GCM {

  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException,
          InvalidAlgorithmParameterException {

    Security.addProvider(new BouncyCastleProvider()); // provedor BC

    // Random IV
    byte[] iv = new byte[128];
    SecureRandom.getInstanceStrong().nextBytes(iv);
    
    // Random key
    KeyGenerator g = KeyGenerator.getInstance("AES", "BC");
    g.init(256); // key size of 256 bits
    Key k = g.generateKey();
    
    GCMParameterSpec gps = new GCMParameterSpec(128, iv);//tag size + iv
    Cipher c = Cipher.getInstance("AES/GCM/NoPadding", "BC");

    // Encriptação pela Ana
    c.init(Cipher.ENCRYPT_MODE, k, gps); //inicializa o AES para encriptacao
    //byte[] textoclaroAna = "Testando o GCM..".getBytes();
    byte[] PT_Ana = U.cancaoDoExilio.getBytes();
    c.updateAAD("AAD not encripted.......".getBytes());
    byte[] ciphertext = c.doFinal(PT_Ana);

    //criptograma[0] = (byte)(criptograma[0]^(byte)0x01);
    
    // decriptação pelo Beto
    c.init(Cipher.DECRYPT_MODE, k, gps); //inicializa o AES para decriptacao
    c.updateAAD("AAD not encripted.......".getBytes());
    boolean ok = true;
    byte[] PT_Beto = null;
    try {
      PT_Beto = c.doFinal(ciphertext);
    } catch (AEADBadTagException e) {
      ok = false;
    }
    if (ok) {
      U.println("Algorithm : " + c.getAlgorithm());
      U.println("tag size  : " + gps.getTLen() + " bits");
      U.println("Ciphertext: " + U.b2x(ciphertext) + ", " + ciphertext.length);
      U.println("Plaintext : " + new String(PT_Beto));
    } else {
      U.println("Tag not valid!");
    }

  }
}