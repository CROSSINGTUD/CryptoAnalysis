package pkm.constPwd4PBE;

import org.alexmbraga.utils.U;
import javax.crypto.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.spec.PBEKeySpec;
import org.bouncycastle.jce.provider.*;


public final class ConstPassword4PBE2 {

  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException,
          InvalidKeySpecException, InvalidAlgorithmParameterException {

    Security.addProvider(new BouncyCastleProvider()); // provedor BC

    // configurações do PBE comuns para Ana e Bato
    char[] senha = new char[] {'5','e','n','h','a','!','2','3'};//8+ alfanum
    byte[] salt = new byte[16];
    (new SecureRandom()).nextBytes(salt);
    int iterationCount = 2048; // 1000+
    PBEKeySpec pbeks = new PBEKeySpec(senha, salt, iterationCount);
    SecretKeyFactory skf
        = SecretKeyFactory.getInstance("PBEWithSHA1And128BitAES-CBC-BC", "BC");
    Key sk = skf.generateSecret(pbeks);
    Cipher c = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");

    // Encriptação pela Ana
    c.init(Cipher.ENCRYPT_MODE, sk);
    byte[] textoclaroAna = "Testando o AES..".getBytes();
    byte[] criptograma = c.doFinal(textoclaroAna);

    // decriptação pelo Beto
    c.init(Cipher.DECRYPT_MODE, sk); //inicializa o AES para decriptacao
    byte[] textoclaroBeto = c.doFinal(criptograma); // Decriptando

    U.println("Chave gerada com: " + skf.getAlgorithm());
    U.println("Encriptado   com: " + c.getAlgorithm());
    //U.println("Chave criptográfica: " + U.b2x(sk.getEncoded()));
    U.println("Init Vector gerado : " + U.b2x(c.getIV()));
    U.println("Criptograma (A-->B): " + U.b2x(criptograma));
    U.println("Texto claro do Beto: " + new String(textoclaroBeto));
  }
}
