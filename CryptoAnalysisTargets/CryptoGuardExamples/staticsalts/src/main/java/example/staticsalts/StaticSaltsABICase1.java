package example.staticsalts;

import javax.crypto.spec.PBEParameterSpec;

public class StaticSaltsABICase1 {

    public static void main(String [] args){
        StaticSaltsABICase1 cs = new StaticSaltsABICase1();
        byte[] salt = {(byte) 0xa2};
        int count = 1020;
        cs.key2(salt,count);

    }
    public void key2(byte[] salt, int count){
        PBEParameterSpec pbeParamSpec = null;
        pbeParamSpec = new PBEParameterSpec(salt, count);
    }
}
