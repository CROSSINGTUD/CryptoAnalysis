package example;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public final class DefinedProvider3 {

  public static void main(String[] args) {
    try {
      SecureRandom r1 = SecureRandom.getInstance("SHA1PRNG","SUN");
      System.out.println("SecureRandom "+r1.getProvider().getName());
      SecureRandom r2 = SecureRandom.getInstanceStrong();
      System.out.println("SecureRandom "+r2.getProvider().getName());
      r1.setSeed(r2.nextLong()); // 64 bits can be a lower bound 
      
    } catch (NoSuchAlgorithmException | NoSuchProviderException e) 
    { System.out.println(e);
    }
  }

}
