
package pdf.insecureComboMacEnc;

import _utils.U;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

//Verificação de integridade e autenticação de mensagem
public final class ManualComboEncryptThenMAC2 {

  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException, 
          InvalidAlgorithmParameterException {

    Security.addProvider(new BouncyCastleProvider()); // provedor BC

    // configurações do sistema criptográfico para Ana e Beto
    byte[] iv = new byte[16], k2 = new byte[16];
    KeyGenerator g = KeyGenerator.getInstance("AES", "BC");
    g.init(256);
    Key k1 = g.generateKey();
    (new SecureRandom()).nextBytes(iv);
    (new SecureRandom()).nextBytes(k2);
    SecretKeySpec sks2 = new SecretKeySpec(k2, "HMACSHA256");
    Cipher c = Cipher.getInstance("AES/CTR/NoPadding", "BC");
    Mac m = Mac.getInstance("HMACSHA256", "BC");
    byte[] ptAna = "De Ana para Beto".getBytes(), X;
    boolean ok, ivo = false;

    // encripta entao autentica: Encrypt-then-MAC (EtM)
    String s = "Encrypt-then-MAC(EtM): calcula a tag do criptograma";
    m.init(sks2); c.init(Cipher.ENCRYPT_MODE, k1, new IvParameterSpec(iv)); 
    byte[] ct = c.doFinal(ptAna);
    byte[] ctPlusIV = ct.clone();
    Arrays.concatenate(ctPlusIV,iv);
    byte[] tag = m.doFinal(ctPlusIV); // calcula a tag do ct+iv

    if (ivo){
      X = U.x_or("De Ana para Beto".getBytes(), "De Ana para  Ivo".getBytes());
      ct = U.x_or(ct, X);
    }
    
    // decriptação pelo Beto com verificação da tag 
    m.init(sks2); c.init(Cipher.DECRYPT_MODE, k1, new IvParameterSpec(iv)); 
    byte[] ptBeto = c.doFinal(ct); // decripta
    ctPlusIV = ct.clone();
    Arrays.concatenate(ctPlusIV,iv);
    ok = MessageDigest.isEqual(m.doFinal(ctPlusIV), tag); // verifica tag
    U.p(s,tag,ptBeto,m,c,ok);
  }
  
}
