package br.predictableSeed;

import java.util.Random;
import java.security.SecureRandom;

public final class LowEntropySeed1 {

  public static void main(String[] args) {
    try {

      System.out.println("Imprevisibilidade - Random");
      SecureRandom r3 = SecureRandom.getInstanceStrong();
      r3.setSeed((new Random()).nextInt());
      SecureRandom r4 = SecureRandom.getInstanceStrong();
      r4.setSeed((new Random()).nextInt());
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
