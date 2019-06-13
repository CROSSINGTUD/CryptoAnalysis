package example.predictableseeds;

import java.security.SecureRandom;

public class PredictableSeedsABICase4 {
    public static final String DEFAULT_SEED = "100";
    private static char[] SEED;
    private static char[] seed;
    public static void main (String [] args){
        go2();
        go3();
        go();

    }

    private static void go2(){
        SEED = DEFAULT_SEED.toCharArray();
    }
    private static void go3(){
        seed = SEED;
    }

    private static void go() {
        SecureRandom sr = new SecureRandom(new byte[]{Byte.parseByte(seed.toString())});
        int v = sr.nextInt();
        System.out.println(v);
    }
}
