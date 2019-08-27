package br.fixedSeed;

import java.security.SecureRandom;

public final class FixedSeed4 {

  public static void main(String[] args) {
    try {
      byte[] fixedSeed = _utils.U.x2b("0123456789ABCDEF0123456789ABCDEF");

      System.out.println("Imprevisibilidade - fixed seed");
      SecureRandom r3 = SecureRandom.getInstanceStrong();
      r3.setSeed(10);
      SecureRandom r4 = SecureRandom.getInstanceStrong();
      r4.setSeed(fixedSeed);
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
