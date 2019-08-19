package example;

//import java.util.Random;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public final class DoNotUseWeakSeed3 {

  // The misuse to avoid here is use low-entropy seeds
  
  public static void main(String[] args) {
    try {
      SecureRandom r1 = SecureRandom.getInstance("SHA1PRNG", "SUN");
      SecureRandom r2 = SecureRandom.getInstanceStrong();
      byte[] seed = new byte[16]; // 128 bits can be a lower bound
      r2.nextBytes(seed);
      r1.setSeed(seed);
      
      // this is for exercising the algorthms 
      for (int i = 0; i < 10; i++) {
        if (i == 0) {   System.out.println("i , r1 , r2"); }
        System.out.println(i+","+r1. nextInt(10000)+","+r2.nextInt(10000));
      }
    } catch (NoSuchAlgorithmException | NoSuchProviderException e) 
    { System.out.println(e);
    }
  }

}
