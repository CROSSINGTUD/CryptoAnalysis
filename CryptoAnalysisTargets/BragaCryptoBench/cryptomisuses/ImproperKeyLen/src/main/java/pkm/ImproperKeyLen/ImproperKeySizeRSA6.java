package pkm.ImproperKeyLen;

import static _utils.U.x2s;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class ImproperKeySizeRSA6 {

  public static void main(String args[]) {

    try {
      Security.addProvider(new BouncyCastleProvider()); // provedor BC
      // Gerando um par de chaves RSA
      KeyPairGenerator gerador = KeyPairGenerator.getInstance("RSA","BC");
      gerador.initialize(128);
      KeyPair chaves = gerador.generateKeyPair();

      //System.out.println("Algoritmo de cifração "
      //        + chaves.getPublic().getAlgorithm());
      //System.out.println("\nFormato da Chave pública "
      //        + chaves.getPublic().getFormat());
      //System.out.println("Chave pública "
      //       + chaves.getPublic().toString());
      //System.out.println("Chave pública codificada "
      //        + x2s(chaves.getPublic().getEncoded()));

      //System.out.println("\nFormato da Chave privada "
      //        + chaves.getPrivate().getFormat());
      //System.out.println("Chave privada "
      //        + chaves.getPrivate().toString());
      //System.out.println("Chave privada codificada "
      //        + x2s(chaves.getPrivate().getEncoded()));

    } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
      System.out.println(e);
    }
  }
}
