package example;

import example._utils.U;
import javax.crypto.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.spec.*;
import javax.crypto.spec.PSource;
import org.bouncycastle.jce.provider.*;

public final class SecureConfig128bitsRSA_4096x384_1 {

  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException,
          InvalidAlgorithmParameterException {

    Security.addProvider(new BouncyCastleProvider()); // provedor BC

    int ksize = 4096; // tamanho da chave RSA
    int hsize = 384; // tamanho do hash 
    int maxLenBytes = (ksize - 2 * hsize) / 8 - 2; // tamanho máximo do texto claro 
    
    // Beto cria um par de chaves
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
    kpg.initialize(ksize);
    KeyPair kp = kpg.generateKeyPair(); 
    
    // configurações comuns para Ana e Bato
    MGF1ParameterSpec mgf1ps = MGF1ParameterSpec.SHA384;
    OAEPParameterSpec OAEPps = new OAEPParameterSpec("SHA-384", "MGF1",
            mgf1ps, PSource.PSpecified.DEFAULT);
    Cipher c = Cipher.getInstance("RSA/None/OAEPPadding", "BC");
    
    // Encriptação pela Ana com a chabe pública de Beto
    Key pubk = kp.getPublic();
    c.init(Cipher.ENCRYPT_MODE, pubk, OAEPps);
    byte[] ptAna = U.cancaoDoExilio.substring(0, maxLenBytes).getBytes();
    byte[] ct = c.doFinal(ptAna);

    // decriptação pelo Beto com sua chave privada
    Key privk = kp.getPrivate();
    c.init(Cipher.DECRYPT_MODE, privk, OAEPps); //inicializa o AES para decriptacao
    byte[] ptBeto = c.doFinal(ct); // Decriptando

    U.println("Chave pública: " + pubk);
    //U.println("Chave privada: " + privk);

    U.println("Encriptado com: " + c.getAlgorithm());
    U.println("Texto claro  da Ana: " + U.b2s(ptAna));
    U.println("Criptograma (A-->B): " + U.b2x(ct) + ", bits " + ct.length * 8);
    U.println("Texto claro do Beto: " + new String(ptBeto));
  }

}

