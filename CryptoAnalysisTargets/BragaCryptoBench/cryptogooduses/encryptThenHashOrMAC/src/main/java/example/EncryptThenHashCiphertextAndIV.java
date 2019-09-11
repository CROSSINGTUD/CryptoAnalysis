
package example;

import org.alexmbraga.utils.U;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

public final class EncryptThenHashCiphertextAndIV {

  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException, 
          InvalidAlgorithmParameterException {

    Security.addProvider(new BouncyCastleProvider()); // provedor BC

    // configurações do sistema criptográfico para Ana e Beto
    byte[] iv = new byte[16];
    KeyGenerator g = KeyGenerator.getInstance("AES", "BC");
    g.init(256);
    Key k = g.generateKey();
    SecureRandom.getInstanceStrong().nextBytes(iv);
    Cipher c = Cipher.getInstance("AES/CTR/NoPadding", "BC");
    MessageDigest md = MessageDigest.getInstance("SHA256", "BC");
    byte[] ptAna = "De Ana para Beto".getBytes(), X, Y;
    
    boolean ok, ivo = false, corrupt = false;

    // encripta entao hash: Encrypt-then-Hash (EtH) 
    String s = "Encrypt-then-Hash(EtH): hash of ciphertext + IV";
    md.reset(); c.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv)); 
    byte[] ct = c.doFinal(ptAna);
    byte[] ctPlusIV = ct.clone();
    Arrays.concatenate(ctPlusIV,iv);
    byte[] hash = md.digest(ctPlusIV);

    if (ivo){
      X = U.x_or("De Ana para Beto".getBytes(), "De Ana para  Ivo".getBytes());
      ct = U.x_or(ct, X);
      hash = md.digest(ct);
    }
    
    if (corrupt){ ct[0] =(byte) ((byte) ct[0]^0x01);}
    
    // this is able to catch corruptions, bot not substitutions
    ctPlusIV = ct.clone();
    Arrays.concatenate(ctPlusIV, iv);
    ok = MessageDigest.isEqual(md.digest(ctPlusIV), hash); // verifica hash
    if (ok) {
      md.reset();
      c.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv));
      byte[] ptBeto = c.doFinal(ct); // decripta
      U.p(s, hash, ptBeto, md, c, ok);
    } else {
    U.println("Do not match!");
    }
  }
  
}
