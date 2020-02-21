package example;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


public final class PSSwSHA384Signature {

  public static void main(String[] args) throws Exception {
    
    Security.addProvider(new BouncyCastleProvider());
    
    KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA", "BC");
    kg.initialize(2048, new SecureRandom());
    KeyPair kp = kg.generateKeyPair();
    Signature sig = Signature.getInstance("SHA384withRSAandMGF1", "BC");
    PSSParameterSpec spec = new PSSParameterSpec("SHA-384", "MGF1", 
            MGF1ParameterSpec.SHA384, 20, 1);
    sig.setParameter(spec);

    byte[] m = "Testing RSA PSS w/ SHA384".getBytes("UTF-8");
    
    sig.initSign(kp.getPrivate(), new SecureRandom());
    sig.update(m);
    byte[] s = sig.sign();

    sig.initVerify(kp.getPublic());
    sig.update(m);

  }
}
