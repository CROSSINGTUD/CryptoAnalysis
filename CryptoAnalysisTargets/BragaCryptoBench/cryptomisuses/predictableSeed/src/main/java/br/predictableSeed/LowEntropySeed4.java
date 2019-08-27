package br.predictableSeed;

import java.security.SecureRandom;

public final class LowEntropySeed4 {

  public static void main(String[] args) {
    try {

      System.out.println("Imprevisibilidade - system.time()");
      SecureRandom r3 = new SecureRandom();
      r3.setSeed(System.nanoTime());
      SecureRandom r4 = new SecureRandom();
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
