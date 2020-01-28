package example;


import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public final class DoNotUseWeakSeed1 {
  
  public static void main(String[] args) {
    try {
      SecureRandom r1 = SecureRandom.getInstance("SHA1PRNG", "SUN");
      SecureRandom r2 = SecureRandom.getInstanceStrong();
      r1.setSeed(r2.nextLong());
      
    } catch (NoSuchAlgorithmException | NoSuchProviderException e) {}
  }
}
