package pdf.insecureComboMacEnc;

import _utils.U;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

//Verificação de integridade e autenticação de mensagem
public final class ManualComboEncryptAndMAC {

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

    // encripta e autentica: Encrypt-and-MAC (E&M)  - a tag é determinística
    String s = "Encrypt-and-MAC (E&M): calcula a tag do texto claro";
    m.init(sks2);
    c.init(Cipher.ENCRYPT_MODE, k1, new IvParameterSpec(iv));
    byte[] ct = c.doFinal(ptAna); // encripta
    byte[] tag = m.doFinal(ptAna); // calcula a tag do texto claro

    if (ivo) {
      X = U.x_or("De Ana para Beto".getBytes(), "De Ana para  Ivo".getBytes());
      ct = U.x_or(ct, X);
    }

    // decriptação pelo Beto com verificação da tag 
    m.init(sks2);
    c.init(Cipher.DECRYPT_MODE, k1, new IvParameterSpec(iv));
    byte[] ptBeto = c.doFinal(ct); // decripta
    ok = MessageDigest.isEqual(m.doFinal(ptBeto), tag);// verifica tag
    U.p(s, tag, ptBeto, m, c, ok);

  }

}
