package example.predictableseeds;

import java.security.SecureRandom;

public class PredictableSeedsABICase1 {
    public static void main (String [] args){
        //long seed = 456789L;
        byte [] seed = {(byte) 100, (byte) 200};
        go(seed);
    }
    private static void go(byte [] seed) {
        SecureRandom sr = new SecureRandom();
        sr.setSeed(seed);
        int v = sr.nextInt();
        System.out.println(v);
    }
}
