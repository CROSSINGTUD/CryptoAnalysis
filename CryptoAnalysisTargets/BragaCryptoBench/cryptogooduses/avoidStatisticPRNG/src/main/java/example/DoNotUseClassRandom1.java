package example;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public final class DoNotUseClassRandom1 {
  
  public static void main(String[] args) {
    try {
      SecureRandom r1 = SecureRandom.getInstance("SHA1PRNG","SUN");
      SecureRandom r2 = SecureRandom.getInstance("SHA1PRNG","SUN");
      
    } catch (NoSuchAlgorithmException | NoSuchProviderException e) {}
  }
}
