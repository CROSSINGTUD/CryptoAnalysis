package br.statisticPRNG;

public final class UsingRandom3 {

  public static void main(String[] args) {
    try {
      System.out.println("Dispersão - Math.random");
      for (int i = 0; i < 100; i++) {
        if (i == 0) {
          System.out.println("i , math.r");
        }
        System.out.println(i + ", " + (int) (Math.random() * 10000));
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }

}
