package ivm.nonRandonIVCBC;

import org.alexmbraga.utils.U;
import java.security.InvalidAlgorithmParameterException;
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
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

// iv não aleatorio ou reutilizado no modo CBC 
public final class NonRandomIVCBC2 {

  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException,
          InvalidAlgorithmParameterException {

    Security.addProvider(new BouncyCastleProvider()); // provedor BC
    byte[] ptAna = ("static counter..").getBytes();

    byte[] iv = U.x2b("0123456789ABCDEF0123456789ABCDEF");

    KeyGenerator g = KeyGenerator.getInstance("AES", "BC");
    g.init(128);
    Key k = g.generateKey();

    U.println("Texto claro : " + new String(ptAna));
    //U.println("Chave       : " + U.b2x(k.getEncoded()));
    U.println("counter fixo: " + U.b2x(iv) + "\n");

    Cipher enc = Cipher.getInstance("AES/CBC/NoPadding", "BC");
    Cipher dec = Cipher.getInstance("AES/CBC/NoPadding", "BC");
    U.println("Encriptado com: " + enc.getAlgorithm());
    byte[] ct;

    enc.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv));
    ct = enc.doFinal(ptAna);
    byte[] iv2 = enc.getIV();
    dec.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv2));
    byte[] ptBeto = dec.doFinal(ct);
    U.println("Criptograma   : " + U.b2x(ct));
    U.println("Texto claro   : " + new String(ptBeto));
    
    enc.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv));
    ct = enc.doFinal(ptAna);
    iv2 = enc.getIV();
    dec.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv2));
    ptBeto = dec.doFinal(ct);
    U.println("Criptograma   : " + U.b2x(ct));
    U.println("Texto claro   : " + new String(ptBeto));
  }
}
