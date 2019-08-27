package br.statisticPRNG;

import java.util.Random;

public final class UsingRandom1 {

  public static void main(String[] args) {
    try {
      System.out.println("Dispersão estatística - Random");
      Random r1 = new Random();
      Random r2 = new Random();
      for (int i = 0; i < 100; i++) {
        if (i == 0) {
          System.out.println("i , r1 , r2");
        }
        System.out.println(i + "," + r1.nextInt(10000) + ","
                + r2.nextInt(10000));
      }

    } catch (Exception e) {
      System.out.println(e);
    }
  }

}
