
package pdf.insecureComboHashEnc;

import org.alexmbraga.utils.U;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

//Combinação de integridade e encriptação
public final class ManualComboEncryptAndHash {

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

    // encripta e hash: Encrypt-and-Hash (E&H) 
    String s = "Encrypt-and-Hash (E&H): calcula o hash do texto claro";
    md.reset(); c.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv)); 
    byte [] ct = c.doFinal(ptAna); // encripta
    byte[] hash = md.digest(ptAna); // calcula a hash do texto claro

    if (ivo){
      X = U.x_or(ptAna, "De Ana para  Ivo".getBytes());
      ct = U.x_or(ct, X);
      hash = md.digest("De Ana para  Ivo".getBytes());
    }
    
    // decriptação pelo Beto com verificação da hash 
    md.reset(); c.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv)); 
    byte[] ptBeto = c.doFinal(ct); // decripta
    ok = MessageDigest.isEqual(md.digest(ptBeto), hash);// verifica hash
    if (ok) { U.p(s,hash,ptBeto,md,c,ok);}
    else    { System.out.println("Verificação de integridade falhou!");}
  }
  
}
