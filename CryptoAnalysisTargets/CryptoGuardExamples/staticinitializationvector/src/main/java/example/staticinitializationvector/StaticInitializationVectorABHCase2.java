package example.staticinitializationvector;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class StaticInitializationVectorABHCase2 {
    public void go() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        SecretKey key = keyGen.generateKey();
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        String name = "abcdef";
        Map<String,String> hm = new HashMap<String, String>();
        hm.put("aaa", "abcde");
        hm.put("bbb", "fghij");
        hm.put("ccc", "klmno");
        hm.put("ddd", "pqrst");

        String str = hm.get("aaa");

        byte [] bytes = str.getBytes();

        IvParameterSpec ivSpec = new IvParameterSpec(bytes);

        cipher.init(Cipher.ENCRYPT_MODE,key,ivSpec);
    }

    public static void main (String [] args) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        StaticInitializationVectorABHCase2 siv = new StaticInitializationVectorABHCase2();
        siv.go();
    }
}
