package pdf.insecureComboMacEnc;

import _utils.U;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

//Verificação de integridade e autenticação de mensagem
public final class ManualComboEncryptThenMAC1 {

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
    byte[] textoclaroAna = "De Ana para Beto".getBytes(), X;
    boolean ok, ivo = false;

    // encripta entao autentica: Encrypt-then-MAC (EtM) 
    String s = "Encrypt-then-MAC(EtM): calcula a tag do criptograma";
    m.init(sks2);
    c.init(Cipher.ENCRYPT_MODE, k1, new IvParameterSpec(iv));
    byte[] criptograma = c.doFinal(textoclaroAna);
    byte[] tag = m.doFinal(criptograma); // calcula a tag do criptograma, sem iv

    if (ivo) {
      X = U.x_or("De Ana para Beto".getBytes(), "De Ana para  Ivo".getBytes());
      criptograma = U.x_or(criptograma, X);
    }

    // decriptação pelo Beto com verificação da tag
    ok = MessageDigest.isEqual(m.doFinal(criptograma), tag); // verifica tag
    if (ok) {
      m.init(sks2);
      c.init(Cipher.DECRYPT_MODE, k1, new IvParameterSpec(iv));
      byte[] textoclaroBeto = c.doFinal(criptograma); // decripta
      U.p(s, tag, textoclaroBeto, m, c, ok);
    } else {
      System.out.println("Autenticaçao da tag falhou!");
    }

  }

}
