package br.predictableSeed;

import java.security.SecureRandom;

public final class LowEntropySeed3 {

  public static void main(String[] args) {
    try {

      System.out.println("Imprevisibilidade - system.time()");
      SecureRandom r3 = SecureRandom.getInstanceStrong();
      r3.setSeed(System.currentTimeMillis());
      SecureRandom r4 = SecureRandom.getInstanceStrong();
      r4.setSeed(System.nanoTime());
      for (int i = 0; i < 100; i++) {
        if (i == 0) {
          System.out.println("i , r3 , r4");
        }
        System.out.println(i + "," + r3.nextInt(10000) + ","
                + r4.nextInt(10000));
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }

}
