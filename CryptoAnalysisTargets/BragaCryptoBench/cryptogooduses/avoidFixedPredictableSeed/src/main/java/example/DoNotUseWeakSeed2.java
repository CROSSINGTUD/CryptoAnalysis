package example;

//import java.util.Random;
import java.security.SecureRandom;

public final class DoNotUseWeakSeed2 {

  // The misuse to avoid here is use low-entropy seeds
  
  public static void main(String[] args) {
    try {
      // supossed to use a defaults seed of 128 bits
      SecureRandom r1 = SecureRandom.getInstanceStrong(); 
      SecureRandom r2 = SecureRandom.getInstanceStrong();
      
      // this is for exercising the algorthms 
      for (int i = 0; i < 10; i++) {
        if (i == 0) {   System.out.println("i , r1 , r2"); }
        System.out.println(i+","+r1. nextInt(10000)+","+r2.nextInt(10000));
      }
    } catch (Exception e) { System.out.println(e);
    }
  }

}
