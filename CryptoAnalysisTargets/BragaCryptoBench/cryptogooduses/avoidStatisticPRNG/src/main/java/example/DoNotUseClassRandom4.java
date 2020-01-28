package example;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public final class DoNotUseClassRandom4 {

  
  public static void main(String[] args) {
    try {
      SecureRandom r1 = SecureRandom.getInstance("Windows-PRNG","SunMSCAPI");
      SecureRandom r2 = SecureRandom.getInstance("Windows-PRNG","SunMSCAPI");
      
    } catch (NoSuchAlgorithmException | NoSuchProviderException e) {}
  }
}
