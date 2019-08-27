package pkc.enc.weakConfigsRSA;

import _utils.U;
import javax.crypto.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.spec.*;
import javax.crypto.spec.PSource;
import org.bouncycastle.jce.provider.*;

// Encriptação e decriptação com chave assimétrica
public final class ImproperConfigRSA_384x160_1 {

  public static void main(String args[]) throws NoSuchAlgorithmException,
          NoSuchPaddingException, InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, NoSuchProviderException,
          InvalidAlgorithmParameterException {

    Security.addProvider(new BouncyCastleProvider()); // provedor BC

    // configurações do sistema criptográfico para Ana e Beto
    int ksize = 384; // tamanho da chave RSA
    int hsize = 160; // tamanho do hash 
    String rsaName = "RSA/None/OAEPPadding";
    int maxLenBytes = (ksize - 2 * hsize) / 8 - 2; // tamanho máximo do texto claro 
    
    // Beto cria um par de chaves
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
    kpg.initialize(ksize);
    KeyPair kp = kpg.generateKeyPair(); 
    
    // configurações comuns para Ana e Bato
    MGF1ParameterSpec mgf1ps = MGF1ParameterSpec.SHA1;
    OAEPParameterSpec OAEPps = new OAEPParameterSpec("SHA1", "MGF1",
            mgf1ps, PSource.PSpecified.DEFAULT);
    Cipher c = Cipher.getInstance(rsaName, "BC");

    
    
    // Encriptação pela Ana com a chabe pública de Beto
    //Key pubk = kp.getPublic();
    c.init(Cipher.ENCRYPT_MODE, kp.getPublic(), OAEPps);
    byte[] ptAna = U.cancaoDoExilio.substring(0, maxLenBytes).getBytes();
    byte[] ct = c.doFinal(ptAna);

    // decriptação pelo Beto com sua chave privada
    //Key privk = kp.getPrivate();
    c.init(Cipher.DECRYPT_MODE, kp.getPrivate(), OAEPps); //inicializa o AES para decriptacao
    byte[] ptBeto = c.doFinal(ct); // Decriptando

    //U.println("Chave pública: " + kp.getPublic());
    //U.println("Chave privada: " + kp.getPrivate());

    U.println("Encriptado com: " + c.getAlgorithm());
    U.println("Texto claro  da Ana: " + U.b2s(ptAna));
    U.println("Criptograma (A-->B): " + U.b2x(ct) + ", bits " + ct.length * 8);
    U.println("Texto claro do Beto: " + new String(ptBeto));
  }

}

/*
 Chave (bits)	Hash(Bits)	TC max(bits)	TC max(bytes)
 384             160             48              6
 512             160             176             22
 768             160             432             54
 768             256             240             30
 1024            160             688             86
 1024            256             496             62
 1024            384             240             30
 2048            160             1712            214
 2048            256             1520            190
 2048            384             1264            158
 2048            512             1008            126
 3096            160             2760            345
 3096            256             2568            321
 3096            384             2312            289
 3096            512             2056            257
 */
