package example.predictableseeds;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class PredictableSeedsABHCase4 {
    public static void main (String [] args){
        //byte seed = 100;
        SecureRandom random = new SecureRandom();
        String str = String.valueOf(random.ints());
        byte[] seed = str.getBytes();

        Map<String,Byte> hm = new HashMap<String, Byte>();
        hm.put("aaa", new Byte((byte) 100));
        hm.put("bbb", new Byte((byte) 200));
        hm.put("ccc", new Byte((byte) 300));
        hm.put("ddd", new Byte((byte) 400));

        byte b = hm.get("aaa");

        SecureRandom sr = new SecureRandom(new byte[]{b});
        int v = sr.nextInt();
        System.out.println(v);
    }

}
