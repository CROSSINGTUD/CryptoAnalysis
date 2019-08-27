package br.statisticPRNG;

import java.util.Random;
import java.security.SecureRandom;

public final class UsingRandom2 {

  public static void main(String[] args) {
    try {

      System.out.println("Imprevisibilidade - Random");
      Random r3 = new Random();
      r3.setSeed((SecureRandom.getInstanceStrong()).nextInt());
      Random r4 = new Random();
      r4.setSeed((SecureRandom.getInstanceStrong()).nextInt());
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
