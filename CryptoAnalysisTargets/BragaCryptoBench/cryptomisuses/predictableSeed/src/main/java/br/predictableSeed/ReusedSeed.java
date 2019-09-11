package br.predictableSeed;

import org.alexmbraga.utils.U;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public final class ReusedSeed {

  public static void main(String[] args) {
    try {
      System.out.println("Imprevisibilidade - SecureRandom - SUN");
      SecureRandom sr4 = SecureRandom.getInstance("SHA1PRNG", "SUN");
      SecureRandom sr5 = SecureRandom.getInstance("SHA1PRNG", "SUN");
      byte[] seed = sr4.generateSeed(32);
      sr4.setSeed(seed);
      sr5.setSeed(seed); // mesma semente
      System.out.println("i , sr4 , sr5");
      for (int i = 0; i < 100; i++) {
        U.println(i + "," + sr4.nextInt(10000) + "," + sr5.nextInt(10000));
      }

      System.out.println("Imprevisibilidade - SecureRandom - MSCAPI");
      SecureRandom sr9 = SecureRandom.getInstance("Windows-PRNG", "SunMSCAPI");
      SecureRandom sr0 = SecureRandom.getInstance("Windows-PRNG", "SunMSCAPI");

      sr9.setSeed(seed); // mesma semente
      sr0.setSeed(seed);
      for (int i = 0; i < 100; i++) {
        if (i == 0) {
          System.out.println("i , sr9 , sr0");
        }
        System.out.println(i + "," + sr9.nextInt(10000) + ","
                + sr0.nextInt(10000));
      }

    } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
      System.out.println(e);
    }
  }
}
