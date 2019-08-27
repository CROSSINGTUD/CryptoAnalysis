package pdf.insecureComboHashEnc;

import _utils.U;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

//Combinação de integridade e encriptação
public final class ManualComboEncryptThenHash1 {

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

    // encripta entao hash: Encrypt-then-Hash(EtH), but IV is unprotected 
    // IV is not included in hash computation
    String s = "Encrypt-then-Hash(EtH): calcula o hash do criptograma";
    md.reset();
    c.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv));
    byte[] ct = c.doFinal(ptAna);
    byte[] hash = md.digest(ct); // calcula a hash soh do ct, sem o iv

    if (ivo) {
      X = U.x_or("De Ana para Beto".getBytes(), "De Ana para  Ivo".getBytes());
      ct = U.x_or(ct, X);
      hash = md.digest(ct);
    }

    // decriptação pelo Beto com verificação da hash
    ok = MessageDigest.isEqual(md.digest(ct), hash); // verifica hash
    if (ok) {
      md.reset();
      c.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv));
      byte[] ptBeto = c.doFinal(ct); // decripta
      U.p(s, hash, ptBeto, md, c, ok);
    } else {
      System.out.println("Verificação de integridade falhou!");
    }
  }
}
