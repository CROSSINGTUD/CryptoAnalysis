
package pdf.insecureComboHashEnc;

import _utils.U;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

//Combinação de integridade e encriptação
public final class ManualComboEncryptThenHash2 {

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
    (new SecureRandom()).nextBytes(iv);
    Cipher c = Cipher.getInstance("AES/CTR/NoPadding", "BC");
    MessageDigest md = MessageDigest.getInstance("SHA256", "BC");
    byte[] ptAna = "De Ana para Beto".getBytes(), X, Y;
    boolean ok, ivo = true;

    // encripta entao hash: Encrypt-then-Hash (EtH) 
    String s = "Encrypt-then-Hash(EtH): calcula o hash do criptograma";
    md.reset(); c.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv)); 
    byte[] ct = c.doFinal(ptAna);
    byte[] ctPlusIV = ct.clone();
    Arrays.concatenate(ctPlusIV,iv);
    byte[] hash = md.digest(ctPlusIV); // calcula a hash do ct+iv

    if (ivo){
      X = U.x_or("De Ana para Beto".getBytes(), "De Ana para  Ivo".getBytes());
      ct = U.x_or(ct, X);
      hash = md.digest(ct);
    }
    
    // decriptação pelo Beto com verificação da hash 
    md.reset(); c.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv)); 
    byte[] ptBeto = c.doFinal(ct); // decripta
    ctPlusIV = ct.clone();
    Arrays.concatenate(ctPlusIV,iv);
    ok = MessageDigest.isEqual(md.digest(ctPlusIV), hash); // verifica hash
    U.p(s,hash,ptBeto,md,c,ok);
    
    
  }
  
}
