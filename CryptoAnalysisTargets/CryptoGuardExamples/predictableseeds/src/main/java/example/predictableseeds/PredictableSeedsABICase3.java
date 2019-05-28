package example.predictableseeds;

import java.security.SecureRandom;

public class PredictableSeedsABICase3 {
    //public static final String DEFAULT_SEED = "456789";
    public static final byte [] DEFAULT_SEED = {(byte) 100, (byte) 200};
    private static byte[] SEED;
    private static byte[] seed;
    public static void main (String [] args){
        go2();
        go3();
        go();
    }

    private static void go2(){
        SEED = DEFAULT_SEED;
    }
    private static void go3(){
        seed = SEED;
    }

    private static void go() {
        SecureRandom sr = new SecureRandom();
        sr.setSeed(seed);
        int v = sr.nextInt();
        System.out.println(v);
    }
}
