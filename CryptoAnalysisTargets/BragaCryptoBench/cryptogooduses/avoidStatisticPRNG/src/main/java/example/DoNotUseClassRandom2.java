package example;

import java.security.SecureRandom;

public final class DoNotUseClassRandom2 {

  
  public static void main(String[] args) {
    try {
      SecureRandom r1 = SecureRandom.getInstanceStrong();
      SecureRandom r2 = SecureRandom.getInstanceStrong();
      
    } catch (Exception e) {}
  }
}
