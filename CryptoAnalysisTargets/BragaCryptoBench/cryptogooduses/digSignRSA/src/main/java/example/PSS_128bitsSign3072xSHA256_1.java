package example;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.PSSParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


public final class PSS_128bitsSign3072xSHA256_1 {

  public static void main(String[] args) throws Exception {
    
    Security.addProvider(new BouncyCastleProvider());
    
    KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA", "BC");
    kg.initialize(3072, new SecureRandom());
    KeyPair kp = kg.generateKeyPair();
    Signature sig = Signature.getInstance("SHA256withRSAandMGF1", "BC");
    sig.setParameter(PSSParameterSpec.DEFAULT);

    byte[] m = "Testing weak RSA-PSS".getBytes("UTF-8");
    
    sig.initSign(kp.getPrivate(), new SecureRandom());
    sig.update(m);
    byte[] s = sig.sign();

    sig.initVerify(kp.getPublic());
    sig.update(m);

  }
}
