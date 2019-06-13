package example.predictablepbepassword;

import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.SecureRandom;

public class PredictablePBEPasswordABSCase1 {
    CryptoPredictablePBE crypto;
    public PredictablePBEPasswordABSCase1(){
        String password = "sagar";
        crypto = new CryptoPredictablePBE(password);
        crypto.encrypt("");
    }
}


class CryptoPredictablePBE {
    String defPassword;
    private PBEKeySpec pbeKeySpec = null;
    private PBEParameterSpec pbeParamSpec = null;

    public CryptoPredictablePBE(String password){

        defPassword = password;
    }

    public void encrypt(String passedPassword) {

        if(passedPassword.isEmpty()){
            passedPassword = defPassword;
        }
        byte [] salt = new byte[16];
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(salt);
        int iterationCount = 11010;
        int keyLength = 16;
        pbeKeySpec = new PBEKeySpec(passedPassword.toCharArray(),salt,iterationCount,keyLength);
    }
}