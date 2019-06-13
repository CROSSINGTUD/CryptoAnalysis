package example.predictableseeds;

import java.security.SecureRandom;

public class PredictableSeedsCorrected {
    public static void main (String [] args){
        SecureRandom sr = new SecureRandom();
        long l = sr.nextLong();
        sr.setSeed(l);
        int v = sr.nextInt();
        System.out.println(v);
    }
}
