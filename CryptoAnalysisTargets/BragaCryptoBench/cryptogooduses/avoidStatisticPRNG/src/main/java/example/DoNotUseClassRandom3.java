package example;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public final class DoNotUseClassRandom3 {

  
  public static void main(String[] args) {
    try {
      SecureRandom r1 = SecureRandom.getInstance("NativePRNG","SUN");
      SecureRandom r2 = SecureRandom.getInstance("NativePRNG","SUN");
      
    } catch (NoSuchAlgorithmException | NoSuchProviderException e) {}
  }
}
