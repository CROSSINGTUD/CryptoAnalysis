package example.staticsalts;

import javax.crypto.spec.PBEParameterSpec;

public class StaticSaltsBBCase1 {
    public static void main (String [] args){
        StaticSaltsBBCase1 cs = new StaticSaltsBBCase1();
        cs.key2();
    }

    public void key2(){
        PBEParameterSpec pbeParamSpec = null;
        byte[] salt = {(byte) 0xa2};
        int count = 1020;
        pbeParamSpec = new PBEParameterSpec(salt, count);
    }
}
