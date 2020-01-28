package example;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public final class DoNotUseWeakSeed4 {
  
  public static void main(String[] args) {
    try {
      SecureRandom r1 = SecureRandom.getInstance("SHA1PRNG", "SUN");
      SecureRandom r2 = SecureRandom.getInstanceStrong();
      byte[] seed = new byte[16];
      r1.setSeed(r2.generateSeed(16));
       
    } catch (NoSuchAlgorithmException | NoSuchProviderException e) {}
  }
}
