package issueseeds;

import java.security.SecureRandom;

public class Main {
	  public static void main (String [] args){
        SecureRandom sr = new SecureRandom();
        byte [] bytes = {(byte) 100, (byte) 200};
        sr.setSeed(bytes);

        int v = sr.nextInt();
        System.out.println(v);
    }
}
