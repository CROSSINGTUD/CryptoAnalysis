
package pdf.insecureComboHashEnc;

import org.alexmbraga.utils.U;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

//Combinação de integridade e encriptação
public final class ManualComboHashThenEncrypt {

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

    // Hash entao encripta: Hash-then-Encrypt (HtE)
    String s = "Hash-then-Encrypt(HtE):calcula o hash do texto claro e encripta a tag";
    md.reset(); c.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv)); 
    byte[] hash = md.digest(ptAna);// calcula a hash do texto claro
    byte[] pacote = Arrays.concatenate(ptAna,hash);
    byte[] ct = c.doFinal(pacote); // encrita texto e hash

    if (ivo){
      X = U.x_or(ptAna, "De Ana para  Ivo".getBytes());
      byte[] cripTexto = Arrays.copyOfRange(ct,0,16);
      byte[] cripTag   = Arrays.copyOfRange(ct,16,ct.length);
      md.reset(); byte[] t1 = md.digest("De Ana para Beto".getBytes());
      md.reset(); byte[] t2 = md.digest("De Ana para  Ivo".getBytes());
      Y = U.x_or(t1, t2);
      ct = Arrays.concatenate(U.x_or(cripTexto, X),U.x_or(cripTag,Y));
    }
    // decriptação pelo Beto com verificação da hash 
    md.reset(); c.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv)); 
    pacote = c.doFinal(ct); // decript texto e hash
    byte[] ptBeto = Arrays.copyOfRange(pacote, 0, 16);
    hash = Arrays.copyOfRange(pacote, 16, pacote.length);
    ok = MessageDigest.isEqual(md.digest(ptBeto), hash); // verifica hash
    if (ok) { U.p(s,hash,ptBeto,md,c,ok);}
    else    { System.out.println("Verificação de integridade falhou!");}
  }
  
}
