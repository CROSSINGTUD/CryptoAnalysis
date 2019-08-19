package example;

//import java.util.Random;
import java.security.SecureRandom;

public final class UseSecureDefaultForPRNG {
  
  public static void main(String[] args) {
    try {
      SecureRandom r1 = SecureRandom.getInstanceStrong();// platform dependent 
      SecureRandom r2 = SecureRandom.getInstanceStrong();// platform dependent
      System.out.println("Algorithm: "+r2.getAlgorithm());
      System.out.println("Algorithm: "+r2.getProvider().getName());
      System.out.println("Algorithm: "+r2.getClass().getName());
      
      // this is for exercising the algorthms 
      for (int i = 0; i < 100; i++) {
        if (i == 0) {   System.out.println("i , r1 , r2"); }
        System.out.println(i+","+r1. nextInt(10000)+","+r2.nextInt(10000));
      }
    } catch (Exception e) { System.out.println(e);
    }
  }

}
