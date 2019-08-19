package example;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


public final class PKCS1_112bitsSign2048xSHA256_2 {

  public static void main(String[] args) throws Exception {
    
    Security.addProvider(new BouncyCastleProvider()); // provedor BC
    
    KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA", "BC");
    kg.initialize(2048, new SecureRandom());
    KeyPair kp = kg.generateKeyPair();
    Signature sig = Signature.getInstance("SHA256withRSA", "BC");

    byte[] m = "Testing RSA PKCS1".getBytes("UTF-8");
    
    // generate a signature
    sig.initSign(kp.getPrivate(), new SecureRandom());
    sig.update(m);
    byte[] s = sig.sign();

    // verify a signature
    sig.initVerify(kp.getPublic());
    sig.update(m);

    if (sig.verify(s)) { System.out.println("Verification succeeded.");}
    else               { System.out.println("Verification failed.");   }
  }
}
