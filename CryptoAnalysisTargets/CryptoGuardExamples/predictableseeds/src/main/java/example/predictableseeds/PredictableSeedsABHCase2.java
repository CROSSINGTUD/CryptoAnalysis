package example.predictableseeds;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class PredictableSeedsABHCase2 {
    public static void main (String [] args){
        Map<String,Byte> hm = new HashMap<String, Byte>();
        hm.put("aaa", new Byte((byte) 100));
        hm.put("bbb", new Byte((byte) 200));
        hm.put("ccc", new Byte((byte) 300));
        hm.put("ddd", new Byte((byte) 400));

        byte b = hm.get("aaa");
        byte [] seed = {b,b};
        SecureRandom sr = new SecureRandom();

        sr.setSeed(seed);
        int v = sr.nextInt();
        System.out.println(v);
    }
}
