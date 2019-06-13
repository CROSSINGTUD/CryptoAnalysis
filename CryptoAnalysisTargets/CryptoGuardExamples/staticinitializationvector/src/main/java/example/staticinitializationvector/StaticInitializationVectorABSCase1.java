package example.staticinitializationvector;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class StaticInitializationVectorABSCase1 {
    CryptoStaticIV1 crypto;
    public StaticInitializationVectorABSCase1() {
        byte [] bytes = "abcde".getBytes();
        IvParameterSpec ivSpec = new IvParameterSpec(bytes);
        crypto = new CryptoStaticIV1(ivSpec);
    }
}

class CryptoStaticIV1 {
    IvParameterSpec defIVSpec;

    public CryptoStaticIV1(IvParameterSpec ivSpec) {
        defIVSpec = ivSpec;
    }

    public void encrypt(IvParameterSpec passedIVSpec) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {

        passedIVSpec = defIVSpec;


        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        SecretKey key = keyGen.generateKey();
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE,key,passedIVSpec);

    }
}

